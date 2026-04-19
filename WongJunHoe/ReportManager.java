import java.util.ArrayList;

public class ReportManager {
    private static final int SUMMARY_VALUE_WIDTH = 50;
    private static final int TX_TIMESTAMP_WIDTH = 19;
    private static final int TX_TYPE_WIDTH = 24;
    private static final int TX_AMOUNT_WIDTH = 19;

    public void printMonthlyStatement(BankAccountRecord accountRecord, AccountDetails profile,
                                      ArrayList<Transaction> transactions) {
        double totalIn = 0;
        double totalOut = 0;
        boolean hasTransaction = false;

        String customerName = accountRecord.getUsername();
        if (profile != null && profile.getName() != null && !profile.getName().isBlank()) {
            customerName = profile.getName();
        }
        if (customerName == null || customerName.isBlank()) {
            customerName = "N/A";
        }

        TerminalUI.printCentered("╔══════════════════════════════════════════════════════════════════════╗");
        TerminalUI.printCentered("║                     TAR DIGITAL BANK STATEMENT                       ║");
        TerminalUI.printCentered("╠══════════════════════════════════════════════════════════════════════╣");
        printSummaryRow("Customer Name", customerName.toUpperCase());
        printSummaryRow("Account Number", accountRecord.getAccountNumber());
        printSummaryRow("Account Type", accountRecord.getAccountType());
        printSummaryRow("Current Balance", String.format("RM %.2f", accountRecord.getBalance()));
        printSummaryRow("Account Status", accountRecord.isFrozen() ? "FROZEN" : "ACTIVE");
        if ("Savings".equalsIgnoreCase(accountRecord.getAccountType())) {
            printSummaryRow("Interest Rate", String.format("%.2f%%", accountRecord.getInterestRate()));
        }
        TerminalUI.printCentered("╠═════════════════════╦══════════════════════════╦═════════════════════╣");
        TerminalUI.printCentered(String.format("║ %-19s ║ %-24s ║ %-19s ║", "Timestamp", "Type", "Amount (RM)"));
        TerminalUI.printCentered("╠═════════════════════╬══════════════════════════╬═════════════════════╣");

        for (Transaction transaction : transactions) {
            if (!accountRecord.getAccountNumber().equals(transaction.getAccountNumber())) {
                continue;
            }
            if (accountRecord.getUserUUID() != null
                    && transaction.getUserUUID() != null
                    && !accountRecord.getUserUUID().equals(transaction.getUserUUID())) {
                continue;
            }

            hasTransaction = true;
            String timestamp = fitCell(transaction.getTimestamp(), TX_TIMESTAMP_WIDTH);
            String type = fitCell(transaction.getType(), TX_TYPE_WIDTH);
            String amount = fitCell(String.format("RM %.2f", transaction.getAmount()), TX_AMOUNT_WIDTH);
            TerminalUI.printCentered(String.format("║ %-19s ║ %-24s ║ %-19s ║", timestamp, type, amount));

            if ("Deposit".equalsIgnoreCase(transaction.getType())
                    || "Monthly Interest".equalsIgnoreCase(transaction.getType())) {
                totalIn += transaction.getAmount();
            } else {
                totalOut += transaction.getAmount();
            }
        }

        if (!hasTransaction) {
            TerminalUI.printCentered(String.format("║ %-66s ║", "No transaction records for this account."));
        }

        TerminalUI.printCentered("╠══════════════════════════════════════════════════════════════════════╣");
        printSummaryRow("Total Inflows", String.format("RM %.2f", totalIn));
        printSummaryRow("Total Outflows", String.format("RM %.2f", totalOut));
        TerminalUI.printCentered("╚══════════════════════════════════════════════════════════════════════╝");
    }

    private void printSummaryRow(String label, String value) {
        TerminalUI.printCentered(String.format("║  %-14s : %-50s ║",
                fitCell(label, 14),
                fitCell(value, SUMMARY_VALUE_WIDTH)));
    }

    private String fitCell(String value, int width) {
        String sanitized = sanitizeForTable(value);
        if (sanitized.length() <= width) {
            return sanitized;
        }
        if (width <= 3) {
            return sanitized.substring(0, width);
        }
        return sanitized.substring(0, width - 3) + "...";
    }

    private String sanitizeForTable(String value) {
        if (value == null || value.isBlank()) {
            return "N/A";
        }
        return value
                .replaceAll("[\\r\\n\\t]+", " ")
                .replaceAll("\\s{2,}", " ")
                .trim();
    }
}
