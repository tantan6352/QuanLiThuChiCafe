package service;

import model.Transaction;
import util.CsvUtil;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.util.List;

public class ReportService {
  public File exportCsv(List<Transaction> items, File out) throws IOException {
    return CsvUtil.writeTransactions(items, out);
  }
}