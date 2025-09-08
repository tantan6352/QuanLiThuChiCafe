package util;

import model.Transaction;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CsvUtil {
  public static File writeTransactions(List<Transaction> items, File out) throws IOException {
    try (var writer = new OutputStreamWriter(new FileOutputStream(out), StandardCharsets.UTF_8);
         var printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
           "ID","Ngày giờ","Loại","Danh mụcID","Tài khoảnID","Số tiền","Ghi chú","Người tạo"))) {
      DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
      for (var t : items) {
        printer.printRecord(
          t.getId(),
          t.getOccuredAt().format(fmt),
          t.getType().name(),
          t.getCategoryId(),
          t.getAccountId(),
          t.getAmount().setScale(0, java.math.RoundingMode.HALF_UP).toPlainString(),
          t.getNote(),
          t.getCreatedBy()
        );
      }
    }
    return out;
  }
}