# Copilot Instructions for Banking-System

## Build, test, and lint commands

### Primary run/build flow
- `./compile`  
  Compiles root Java files plus `WongYiHoe/*.java`, `Samuel/*.java`, `Eugene/*.java`, and `WongJunHoe/*.java`, then runs `MainMenu`.

### Compile-only command (non-interactive)
- `javac -cp gson-2.11.0.jar:. *.java WongYiHoe/*.java Samuel/*.java Eugene/*.java WongJunHoe/*.java`

### Run command after manual compile
- `java -cp gson-2.11.0.jar:.:WongYiHoe:Samuel:Eugene:WongJunHoe MainMenu`

### Automated tests and lint
- No automated test suite or linter is configured in this repository.
- No single-test command is available.

## High-level architecture

- **Entrypoint and main flow:** `MainMenu` is the app entrypoint. Users register via `Register` or log in via `Login`.
- **Post-login routing:** `Login` sends admin credentials directly into `AdminMenu`; regular users go to `FirstTimeSetup.setup(user)` for profile/account resolution before entering the account dashboard.
- **Persistence boundary:** `FileManager` is the JSON gateway and owns reads/writes for:
  - `users.json` → `ArrayList<UserCredentials>`
  - `accounts.json` → `ArrayList<AccountDetails>`
  - `bank_accounts.json` → `ArrayList<BankAccountRecord>`
  - `transactions.json` → `ArrayList<Transaction>`
  - `customers.json` → `ArrayList<Customer>`
- **Identity and linking model:** user identity joins primarily on UUID (`UserCredentials.uuid`, `AccountDetails.uuid`, `BankAccountRecord.userUUID`), while account linkage uses `AccountDetails.accountNumber` ↔ `BankAccountRecord.linkedProfileAccountNumber`.
- **Account domain model:** account behavior lives in `WongYiHoe/`:
  - `Account` (base), `SavingsAccount`, `CurrentAccount`
  - `FirstTimeSetup` restores these domain objects from persisted records and syncs updates back to `bank_accounts.json`, while transactions are persisted in `transactions.json`.
- **Admin/reporting module:** `WongJunHoe/AdminMenu` + `ReportManager` implement freeze/unfreeze, interest updates, statement generation, and account deletion over persisted records.

## Key conventions in this codebase

- **Use `MainMenu` as the runtime entrypoint**, not `WongJunHoe/Main.java`.
- **Default package only:** classes are not package-namespaced. Keep new classes package-less unless the whole project is being migrated.
- **Classpath must include all module folders at runtime** (`WongYiHoe:Samuel:Eugene:WongJunHoe`) for classes loaded from those directories.
- **Persist through `FileManager` methods** instead of ad-hoc JSON I/O in feature classes, and save mutated lists back immediately.
- **First-time account numbering is global and sequential:** create new profile/bank account numbers as `ACC<n>` where `n` is max existing numeric suffix + 1 across account/profile records.
- **Keep account linkage aligned:** for new bank records, `linkedProfileAccountNumber` and `accountNumber` should match the profile account number.
- **Menu input style:** this codebase favors reading text with `Scanner.nextLine()` and parsing values, avoiding `nextInt()` newline pitfalls in looped TUIs.
- **Admin delete flow (existing project rule):** Admin option 5 removes the selected bank account plus linked profile and linked transactions; user credentials are deleted only if that user has no other linked bank account.
