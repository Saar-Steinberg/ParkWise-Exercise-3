# ParkWise Exercise 3

ParkWise Exercise 3 is a collaborative Java coursework project that models a desktop parking-management workflow. It is presented as an academic portfolio artifact, not as a production-ready application or a claim of sole authorship.

## What the source implements

- Swing screens for welcome, administrator, customer, and report flows.
- Access-backed loading of parking lots, parking spaces, conveyors, and parking sessions.
- Parking-space allocation through conveyor logic, including occupied-space updates.
- Parking-session pricing and a customer payment flow.
- A JSON price-list payload adapter that forwards JSON content to pricing controllers.
- An annual-report viewer integration using JasperReports.

The gate sensor and credit-card gateway are simulations. `Main.MainApp` uses a fixed sample vehicle identifier for the sensor, and `CreditCardSystem` always approves a payment. These are coursework stubs, not hardware or payment-service integrations.

## Technology and dependencies

- Java 21 - the source was compiled successfully with JDK 21 during repository preparation.
- Java Swing - desktop user interface.
- Microsoft Access through UCanAccess 5.0.1.
- JasperReports 7.0.1 - reporting integration.
- Gson 2.10.1 - retained in `lib/gson-2.10.1.jar`; the current Java source does not directly import Gson.
- Local third-party support JARs in `lib/lib`.

No Maven or Gradle build is included.

## Project structure

```text
src/
|- Main/       application entry point and database connectivity check
|- Boundary/   Swing screens, system interfaces, adapters, report resources
|- Control/    repository, conveyor, payment, pricing, and report logic
|- Entity/     parking-domain objects
|- Enums/      domain enumerations
`- utils/      database connection helper
lib/            retained third-party dependencies
```

`Main.MainApp` is the desktop application entry point. `Main.TestDB` only checks that the Access driver and local database can be opened.

## Prerequisites

1. JDK 21. Use `java.exe` from the same JDK installation as `javac.exe`.
2. A reviewed local `ParkWiseDB.accdb` placed in the repository root. It is required at runtime but deliberately not included in the repository.
3. The retained `lib` directory. Do not remove or rearrange its JARs when reproducing the current classpath.

## Compile and launch (Windows PowerShell)

From the repository root:

```powershell
$jdk = 'C:\path\to\jdk-21\bin'
$sources = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName
New-Item -ItemType Directory -Force out | Out-Null
& "$jdk\javac.exe" -encoding UTF-8 -cp 'lib\*;lib\lib\*' -d out $sources
& "$jdk\java.exe" -cp 'out;src;lib\*;lib\lib\*' Main.MainApp
```

The compilation command above was verified with JDK 21. The launch command follows the actual `Main.MainApp` entry point and includes `src` so `ReportLogic` can load `/Boundary/AnnualReport.jasper` as a classpath resource. It has **not** been runtime-verified against a database; do not run it against data that must remain unchanged.

To run the database connectivity check instead, replace `Main.MainApp` with `Main.TestDB` in the second command. This also opens the local database and was not run during repository preparation.

Windows uses `;` as the classpath separator. On macOS or Linux, replace it with `:`; the desktop application has not been validated on those platforms.

## Validation status and limitations

- Confirmed: all Java source compiled successfully with JDK 21 using `lib\*;lib\lib\*`.
- Not tested: database connectivity, GUI interaction, parking/payment workflows, price-list import execution, or report viewing/export.
- The report logic calls JasperReports to fill and display `AnnualReport.jasper`; successful report rendering or PDF/XML generation has not been verified.
- No automated tests or build automation were found.
- The repository retains legacy and optional-looking third-party libraries because runtime non-use has not been proven. JasperReports 7.0.1 also declares runtime dependencies that are not present by filename in the retained library tree, so reporting may require additional dependency review.

## Local data and repository hygiene

The `.gitignore` excludes the local Access database and lock files, compiled output, packaged application JARs, generated reports, exact duplicate JARs, a documentation-only JAR, and a legacy UCanAccess distribution. Required source, report templates, and the retained `lib/lib` dependency copies remain tracked.

`pricelist.json` is retained as an example price-list payload. Current source accepts JSON content through its adapter/controller path but does not directly read that filename.

Third-party JARs are included for the coursework setup. UCanAccess includes Apache 2.0 licensing information, while JasperReports 7.0.1 includes LGPL v3 licensing information; review the licensing and redistribution terms of all third-party JARs before making the repository public.
