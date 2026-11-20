# Copy to .env.local.ps1 (gitignored) and replace values. Never commit credentials.
$env:SPRING_PROFILES_ACTIVE='local'
$env:DB_HOST='localhost'
$env:DB_PORT='REPLACE_WITH_VERIFIED_TCP_PORT'
$env:DB_USERNAME='sportbooking_app'
$env:DB_PASSWORD='REPLACE_WITH_YOUR_PASSWORD'
$env:DEMO_ENABLED='true'
$env:DEMO_PASSWORD='REPLACE_WITH_YOUR_DEMO_PASSWORD'
