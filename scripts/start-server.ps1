$ErrorActionPreference = 'Stop'

$rootDir = Split-Path -Parent $PSScriptRoot
$envFile = Join-Path $rootDir '.env'

if (-not (Test-Path -LiteralPath $envFile)) {
    throw "Missing $envFile. Copy .env.example to .env and fill in local values."
}

Get-Content -LiteralPath $envFile | Where-Object { $_ -match '^\s*[^#][^=]*=' } | ForEach-Object {
    $name, $value = $_ -split '=', 2
    [Environment]::SetEnvironmentVariable($name.Trim(), $value.Trim())
}

$serverDir = Join-Path $rootDir 'frame-forward-server'
$exitCode = 0
Push-Location $serverDir
try {
    & mvn -pl bootstrap -am package install:install -DskipTests '-Dspring-boot.repackage.skip=true'
    $exitCode = $LASTEXITCODE

    if ($exitCode -eq 0) {
        & mvn -pl bootstrap spring-boot:run '-Dspring-boot.run.profiles=dev'
        $exitCode = $LASTEXITCODE
    }
}
finally {
    Pop-Location
}

exit $exitCode
