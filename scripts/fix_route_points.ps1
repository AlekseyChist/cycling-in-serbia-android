# One-shot data-ops: regenerate route_points for tracks where the column has < 5 points.
# Reads SUPABASE_URL + SUPABASE_ANON_KEY from local.properties (read-only access).
# Downloads GPX files from public Storage bucket, parses <trkpt>, downsamples to ~$TargetPoints
# evenly distributed points, and writes UPDATE SQL to scripts/fix_route_points.sql.
#
# To apply: paste the generated SQL into Supabase Studio -> SQL Editor -> Run.
# Service-role key is never used by this script.

param(
    [int]$TargetPoints = 300,
    [string]$OutFile = "scripts/fix_route_points.sql"
)

$ErrorActionPreference = 'Stop'

# --- creds (read-only audit only) ---
$props = Get-Content local.properties
function Get-Prop($name) {
    $line = ($props | Where-Object { $_ -match "^$name\s*=" }) -replace "^$name\s*=\s*", ''
    $v = $line.Trim().Trim('"').Trim("'")
    return $v -replace '\\:', ':' -replace '\\=', '='
}
$url = Get-Prop 'SUPABASE_URL'
$key = Get-Prop 'SUPABASE_ANON_KEY'
$headers = @{ apikey = $key; Authorization = "Bearer $key" }

# --- find broken rows ---
$resp = Invoke-RestMethod -Uri "$url/rest/v1/tracks?select=id,name,gpx_file_name,route_points&is_published=eq.true" -Headers $headers
$broken = $resp | Where-Object { -not $_.route_points -or $_.route_points.Count -lt 5 }
Write-Host "Broken rows: $($broken.Count)" -ForegroundColor Cyan

# --- helper: parse GPX text -> [{lat, lng}] ---
function Parse-Gpx([string]$gpxXml) {
    $xml = [xml]$gpxXml
    $ns = New-Object System.Xml.XmlNamespaceManager($xml.NameTable)
    $ns.AddNamespace('g', 'http://www.topografix.com/GPX/1/1')
    $points = @()
    # try GPX 1.1 namespace first, then fall back to namespace-less
    $nodes = $xml.SelectNodes('//g:trkpt', $ns)
    if ($nodes.Count -eq 0) { $nodes = $xml.SelectNodes('//trkpt') }
    if ($nodes.Count -eq 0) {
        $nodes = $xml.SelectNodes('//g:rtept', $ns)
        if ($nodes.Count -eq 0) { $nodes = $xml.SelectNodes('//rtept') }
    }
    foreach ($n in $nodes) {
        $points += [pscustomobject]@{
            lat = [double]$n.Attributes['lat'].Value
            lng = [double]$n.Attributes['lon'].Value
        }
    }
    return ,$points
}

# --- helper: downsample to ~$target evenly spaced points ---
function Downsample($points, [int]$target) {
    if ($points.Count -le $target) { return $points }
    $step = [double]($points.Count - 1) / ($target - 1)
    $idx = 0..($target - 1) | ForEach-Object { [int][Math]::Round($_ * $step) }
    return $idx | ForEach-Object { $points[$_] }
}

# --- helper: SQL-escape a UUID/string for inline literal (single-quote only) ---
function Sql-Escape([string]$s) { return $s.Replace("'", "''") }

# --- main loop ---
$sqlLines = @()
$sqlLines += "-- Auto-generated: regenerate route_points from GPX for $($broken.Count) tracks."
$sqlLines += "-- Run inside a transaction; review before COMMIT."
$sqlLines += ""
$sqlLines += "BEGIN;"
$sqlLines += ""

$failures = @()

foreach ($t in $broken) {
    $gpxUrl = "$url/storage/v1/object/public/gpx-files/$($t.gpx_file_name)"
    Write-Host ("Fetching {0,-50} ..." -f $t.name) -NoNewline
    try {
        $gpxText = (Invoke-WebRequest -Uri $gpxUrl -UseBasicParsing).Content
        $points = Parse-Gpx $gpxText
        Write-Host (" parsed={0}" -f $points.Count) -NoNewline
        if ($points.Count -lt 5) {
            Write-Host " SKIP (too few points in GPX)" -ForegroundColor Yellow
            $failures += $t.name
            continue
        }
        $down = Downsample $points $TargetPoints
        Write-Host (" -> {0} points" -f $down.Count) -ForegroundColor Green
        # Manually format with InvariantCulture — Russian locale otherwise emits "43,9" (broken JSON).
        $ic = [System.Globalization.CultureInfo]::InvariantCulture
        $items = $down | ForEach-Object {
            '{"lat":' + $_.lat.ToString($ic) + ',"lng":' + $_.lng.ToString($ic) + '}'
        }
        $jsonArr = '[' + ($items -join ',') + ']'
        $sqlLines += "-- $($t.name)  (was $($t.route_points.Count) -> $($down.Count) points)"
        $sqlLines += "UPDATE tracks SET route_points = '$jsonArr'::jsonb WHERE id = '$(Sql-Escape $t.id)';"
        $sqlLines += ""
    } catch {
        Write-Host " FAILED: $_" -ForegroundColor Red
        $failures += "$($t.name) : $_"
    }
}

$sqlLines += "-- After verifying, replace ROLLBACK with COMMIT below:"
$sqlLines += "ROLLBACK;"

# --- write output ---
$dir = Split-Path -Parent $OutFile
if ($dir -and -not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir | Out-Null }
$sqlLines | Set-Content -Path $OutFile -Encoding UTF8

Write-Host ""
Write-Host "Wrote $OutFile ($($sqlLines.Count) lines)" -ForegroundColor Cyan
if ($failures.Count -gt 0) {
    Write-Host "Failures:" -ForegroundColor Yellow
    $failures | ForEach-Object { Write-Host "  - $_" }
}
