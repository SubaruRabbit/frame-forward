$ErrorActionPreference = 'Stop'

function Invoke-WorkspaceCheck {
    param(
        [string]$WorkingDirectory,
        [string[]]$Command
    )

    Push-Location -LiteralPath $WorkingDirectory
    try {
        & $Command[0] $Command[1..($Command.Length - 1)]
        if ($LASTEXITCODE -ne 0) {
            throw "Check failed: $($Command -join ' ')"
        }
    }
    finally {
        Pop-Location
    }
}

$repositoryRoot = Split-Path -Parent $PSScriptRoot

Invoke-WorkspaceCheck (Join-Path $repositoryRoot 'frame-forward-app') @('npm', 'run', 'typecheck')
Invoke-WorkspaceCheck (Join-Path $repositoryRoot 'frame-forward-server') @('mvn', 'test')
Invoke-WorkspaceCheck (Join-Path $repositoryRoot 'contracts') @('npm', 'run', 'validate')
