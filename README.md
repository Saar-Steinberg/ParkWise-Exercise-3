# ParkWise — Parking Management System

ParkWise is a Java desktop application developed as part of a collaborative information systems coursework project. It models a parking management system, including parking-space management, entry and exit workflows, pricing logic, simulated payment integrations, and annual reporting.

This repository preserves the third exercise in the ParkWise coursework sequence. It is intended for code review and educational reference, not as a production-ready application.

## Key Features

- **Parking Management** — Domain models and repository logic for parking lots, parking spaces, vehicles, and parking sessions.
- **Entry and Exit Workflows** — Application flows involving parking sessions and conveyor/gate interfaces.
- **Pricing and Payments** — Pricing logic and simulated payment and gate integrations.
- **Database Integration** — Microsoft Access connectivity through UCanAccess.
- **Reporting** — JasperReports templates and reporting integration. Report generation has not been runtime-verified.

## Technology Stack

- **Language:** Java (JDK 21)
- **Desktop UI:** Java Swing
- **Database:** Microsoft Access, accessed through UCanAccess 5.0.1
- **Reporting:** JasperReports 7.0.1
- **Additional Libraries:** Gson and supporting third-party JARs

The project does not include a Maven or Gradle build configuration. Dependencies are retained in the repository's `lib/` directory.

## Architecture and Source Layout

The source follows a layered organization that separates the user interface, application logic, and domain objects.

```text
src/
├── Main/       # Application and database-test entry points
├── Boundary/   # Swing screens, interfaces, and adapters
├── Control/    # Parking, pricing, payment, repository, and report logic
├── Entity/     # Domain objects
├── Enums/      # Domain enumerations
└── utils/      # Database connection utilities

lib/            # Third-party dependency JARs
```

- **Boundary** contains the desktop interface and interfaces to external-system simulations.
- **Control** contains application workflows and business logic.
- **Entity** represents the main domain objects used by the system.

## Local Setup

### Requirements

- Windows
- JDK 21
- The existing `lib/` dependency directory
- A compatible local copy of `ParkWiseDB.accdb`

The database is intentionally excluded from this repository. A reviewed local database must be placed in the project root before running database-dependent workflows. Do not use production or personal data.

### Compile and Run

Open PowerShell in the project root. Set `$jdk` to your local JDK 21 `bin` directory:

```powershell
$jdk = 'C:\path\to\jdk-21\bin'

$classpath = 'lib\*;lib\lib\*'

New-Item -ItemType Directory -Force out | Out-Null

$sources = Get-ChildItem src -Recurse -Filter *.java |
    ForEach-Object FullName

& "$jdk\javac.exe" -encoding UTF-8 `
    -cp $classpath `
    -d out `
    $sources
```

If compilation succeeds, launch the application with:

```powershell
$runtimeClasspath = 'out;src;lib\*;lib\lib\*'

& "$jdk\java.exe" `
    -cp $runtimeClasspath `
    Main.MainApp
```

The `src` directory is included on the runtime classpath so that report-template resources can be located.

**Note:** Compilation has been verified with JDK 21. Full runtime setup remains unverified, including all reporting dependencies and database-backed workflows. The commands above document the intended local setup, not a guarantee that every feature runs successfully.

To run the database connectivity check, use the same runtime classpath and replace `Main.MainApp` with `Main.TestDB`.

## Validation Status and Limitations

- Java source compilation succeeded with JDK 21 during repository preparation.
- No automated test suite or Maven/Gradle build configuration was found.
- Database connectivity and GUI workflows have not been runtime-tested.
- Payment and gate integrations are simulations, not live external services.
- JasperReports integration is present in the source, but successful report generation has not been verified.
- The application may write to the local database during normal workflows. Use a disposable, reviewed database copy for testing.

## Coursework and Attribution

This is a collaborative academic project. The repository is preserved for educational reference and code review; it does not claim sole authorship or production readiness.


