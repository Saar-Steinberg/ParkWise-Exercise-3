# ParkWise — Exercise 3

A later collaborative Java coursework exercise that expands the ParkWise parking-management scenario. It is preserved for portfolio review as coursework, not as a production-ready system or a claim of sole authorship.

## Confirmed scope

The source includes:

- Swing welcome, administrator, customer, and report screens;
- parking-lot, parking-space, vehicle, client, conveyor, price-list, and parking-session domain classes;
- Access-backed repository loading for parking lots, spaces, conveyors, and sessions;
- parking entry/exit, pricing, and simulated payment/gate integrations; and
- a JasperReports-based annual-report integration and retained report template files.

The credit-card and gate interactions are simulations in the source. Report behavior has not yet been runtime-verified and should not be represented as a confirmed export workflow.

## Technology

- Java; the supplied application JAR was compiled for Java 21.
- Swing and the Java standard library.
- UCanAccess 5.0.1 for Microsoft Access connectivity.
- JasperReports 7.0.1 and retained supporting JARs.
- Gson 2.10.1.
- Microsoft Access database (`ParkWiseDB.accdb`) as a local runtime prerequisite.

No Maven or Gradle build is included.

## Source layout

```text
src/
├── Main/                    # application and database-test entry points
├── Boundary/                # Swing screens and system interfaces/adapters
├── Control/                 # repository, conveyor, payment, pricing, reporting logic
├── Entity/                  # domain objects
├── Enums/                   # domain enumerations
└── utils/                   # database connection helper
lib/                         # retained runtime and reporting dependency JARs
```

## Local setup

1. Install JDK 21. Invoke `java.exe` from that JDK rather than an older system Java installation.
2. Place a reviewed local copy of `ParkWiseDB.accdb` in the project root. It is deliberately ignored and is not supplied for publication.
3. Retain the provided `lib` directory unchanged; it contains UCanAccess, JasperReports, and supporting JARs used by the existing project.

From PowerShell on Windows:

```powershell
$jdk = 'C:\path\to\jdk-21\bin'
$sources = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName
& "$jdk\javac.exe" -cp 'lib\*;lib\lib\*' -d out $sources
& "$jdk\java.exe" -cp 'out;src;lib\*;lib\lib\*' Main.MainApp
```

`src` is included at runtime so the report template resources remain available. The classpath separator is `;` on Windows and `:` on macOS/Linux. The desktop UI has not been validated outside Windows.

For a database connectivity check, use the same classpath and run `Main.TestDB` instead of `Main.MainApp`.

## Known limitations and validation status

- Runtime behavior depends on an unreviewed local Access database; do not publish it without a data review.
- The application writes through its repository and payment/session workflows; avoid running it against data that must remain unchanged.
- A generated annual-report PDF/XML pair was found locally but is excluded as output, not documentation of tested behavior.
- No automated test suite or build automation was found.
- The source compiled successfully with JDK 21 during repository preparation. No database-backed or GUI runtime check was performed because the local database is unreviewed.

## Repository hygiene

The ignore rules exclude local databases, Access lock files, compiled output, packaged application JARs, and generated reports. The dependency libraries are intentionally retained and not ignored. Review course-sharing rules and obtain all collaborators' consent before publishing any version of this coursework.
