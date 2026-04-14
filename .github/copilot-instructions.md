# Copilot Instructions for Banking-System

## Build, test, and lint commands

### Primary run/build flow
- `./compile`  
  Compiles all root Java files and `WongYiHoe/*.java`, then runs `MainMenu`.

### Equivalent manual commands
- `javac -cp gson-2.11.0.jar:. *.java WongYiHoe/*.java`
- `java -cp gson-2.11.0.jar:.:WongYiHoe MainMenu`

### Automated tests and lint
- No automated test suite or linter is currently configured in this repository.
- There is no single-test command available at this time.

## High-level architecture

- **App entrypoint and flow:** `MainMenu` is the master entrypoint. Users can register (`Register`) or log in (`Login`). Successful login routes into `FirstTimeSetup.setup(user)`, which handles profile/account provisioning and then runs the banking operations dashboard.
- **Persistence layer:** `FileManager` is the JSON gateway and owns all reads/writes:
  - `users.json` → `ArrayList<UserCredentials>`
  - `accounts.json` → `ArrayList<AccountDetails>` (profile + account metadata)
  - `bank_accounts.json` → `ArrayList<BankAccountRecord>` (operational account state like balance)
- **Data relationship (important):**
  - `UserCredentials.uuid` links user identity across all records.
  - `AccountDetails.accountNumber` is linked to `BankAccountRecord.linkedProfileAccountNumber`.
  - `FirstTimeSetup` resolves/creates both profile (`AccountDetails`) and operational account record (`BankAccountRecord`) before account operations.
- **Account domain model:** Account behavior lives in `WongYiHoe/`:
  - `Account` (base), `SavingsAccount`, `CurrentAccount`
  - `FirstTimeSetup` instantiates/restores these domain objects and syncs post-operation balances back into `bank_accounts.json`.

## Key conventions in this codebase

- **Use `MainMenu` as the runtime entrypoint**, not `WongYiHoe/Main.java`.
- **Default package only:** classes are not package-namespaced. Keep new classes package-less unless the whole project is being migrated.
- **Classpath must include `WongYiHoe` at runtime** (`-cp ...:WongYiHoe`) because account domain classes are compiled from that folder.
- **Persist through `FileManager` methods** instead of ad-hoc JSON I/O in feature classes.
- **Identity joins should use UUID + linked account number**, not username alone, when relating profile and bank-account records.
- **Menu input style:** this codebase favors reading text with `Scanner.nextLine()` and parsing values, avoiding `nextInt()` newline pitfalls in looped TUIs.
