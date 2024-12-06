package com.example.Finance.service;

import com.example.Finance.dto.IncomeStatementResponse;
import com.example.Finance.dto.TransactionHistoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class IncomeStatementPdfService extends PdfService {

    private final IncomeStatementService incomeStatementService;

    public byte[] generateIncomeStatementPdf(List<TransactionHistoryResponse> transactionHistoryResponseList) {
        // 손익 계산하기
        IncomeStatementResponse incomeStatementResponse = incomeStatementService.calculateStatement(transactionHistoryResponseList);

        // 게산한 손익관련 자료로 HTML 생성
        String html = generateHtml(transactionHistoryResponseList, incomeStatementResponse);
        log.info("HTML 생성 시간: {}", LocalDateTime.now().toString());

        // HTML을 PDF로 변환
        return convertHtmlToPdf(html);
    }

    private String generateHtml(List<TransactionHistoryResponse> transactions, IncomeStatementResponse incomeStatementResponse) {
        StringBuilder html = new StringBuilder();
        html.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">")
                .append("<html xmlns=\"http://www.w3.org/1999/xhtml\">")
                .append("<head>")
                .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>")
                .append("<style type=\"text/css\">")
                .append("@font-face {")
                .append("    font-family: 'NanumGothic';")
                .append("    src: url('classpath:/fonts/NanumGothic-Regular.ttf') format('truetype');")
                .append("}")
                .append("body { font-family: 'NanumGothic', Arial, sans-serif; margin: 40px; }")
                .append(".title { text-align: center; font-size: 24px; margin-bottom: 30px; font-weight: bold; }")
                .append(".section { margin: 20px 0; }")
                .append(".label { font-size: 14px; font-weight: bold; }")
                .append(".value { text-align: right; font-size: 12px; margin: 5px 0 15px 0; }")
                .append(".divider { border-top: 1px solid #ccc; margin: 10px 0; }")
                .append("</style>")
                .append("</head><body>");

        // 제목
        html.append("<div class=\"title\">손익 계산서</div>");

        // 총 매출액
        Map<String, BigDecimal> revenueDetails = getRevenueDetails(transactions);
        addHtmlSectionWithDetails(html, "총 매출액", incomeStatementResponse.getTotalRevenue(), revenueDetails);
        html.append("<div class=\"divider\"></div>");

        // 매출원가
        Map<String, BigDecimal> costOfSalesDetails = getCostOfSalesDetails(transactions);
        addHtmlSectionWithDetails(html, "매출원가", incomeStatementResponse.getCostOfSales(), costOfSalesDetails);
        html.append("<div class=\"divider\"></div>");

        // 매출총이익
        addHtmlSection(html, "매출총이익", incomeStatementResponse.getGrossProfit());
        html.append("<div class=\"divider\"></div>");

        // 판매관리비
        Map<String, BigDecimal> operatingExpensesDetails = getOperatingExpensesDetails(transactions);
        BigDecimal totalOperatingExpenses = operatingExpensesDetails.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        addHtmlSectionWithDetails(html, "판매관리비", totalOperatingExpenses, operatingExpensesDetails);
        html.append("<div class=\"divider\"></div>");

        // 영업이익
        addHtmlSection(html, "영업이익", incomeStatementResponse.getOperatingIncome());
        html.append("<div class=\"divider\"></div>");

        // 수익률
        addHtmlSectionPercent(html, "수익률", incomeStatementResponse.getProfitMargin());

        html.append("</body></html>");
        return html.toString();
    }

    private void addHtmlSection(StringBuilder html, String label, BigDecimal value) {
        String valueStr = (value != null) ? String.format("%,d원", value.longValue()) : "0원";
        html.append("<div class=\"section\">")
                .append("<div class=\"label\">").append(label).append("</div>")
                .append("<div class=\"value\">").append(valueStr).append("</div>")
                .append("</div>");
    }

    private void addHtmlSectionWithDetails(StringBuilder html, String label, BigDecimal totalValue, Map<String, BigDecimal> details) {
        String totalValueStr = (totalValue != null) ? String.format("%,d원", totalValue.longValue()) : "0원";
        html.append("<div class=\"section\">")
                .append("<div class=\"label\">").append(label).append("</div>")
                .append("<div class=\"value\">").append(totalValueStr).append("</div>")
                .append("</div>");

        for (Map.Entry<String, BigDecimal> entry : details.entrySet()) {
            String detailLabel = entry.getKey();
            BigDecimal detailValue = entry.getValue();
            String detailValueStr = (detailValue != null) ? String.format("%,d원", detailValue.longValue()) : "0원";

            html.append("<div class=\"section\">")
                    .append("<div class=\"label\">").append("  - ").append(detailLabel).append("</div>")
                    .append("<div class=\"value\">").append(detailValueStr).append("</div>")
                    .append("</div>");
        }
    }

    private void addHtmlSectionPercent(StringBuilder html, String label, BigDecimal value) {
        String valueStr = (value != null) ? String.format("%,d%%", value.longValue()) : "0%";
        html.append("<div class=\"section\">")
                .append("<div class=\"label\">").append(label).append("</div>")
                .append("<div class=\"value\">").append(valueStr).append("</div>")
                .append("</div>");
    }

    private Map<String, BigDecimal> getRevenueDetails(List<TransactionHistoryResponse> transactions) {
        return getTransactionDetails(transactions, incomeStatementService::isRevenueClassification);
    }

    private Map<String, BigDecimal> getCostOfSalesDetails(List<TransactionHistoryResponse> transactions) {
        return getTransactionDetails(transactions, incomeStatementService::isCostOfSalesClassification);
    }

    private Map<String, BigDecimal> getOperatingExpensesDetails(List<TransactionHistoryResponse> transactions) {
        return getTransactionDetails(transactions, incomeStatementService::isOperatingExpenseClassification);
    }

    private Map<String, BigDecimal> getTransactionDetails(List<TransactionHistoryResponse> transactions,
                                                          Predicate<String> classificationFilter) {
        Map<String, BigDecimal> details = new LinkedHashMap<>();

        transactions.stream()
                .filter(t -> !t.getIsDeposit()) //매출액이 아닐때만 동작하게
                .filter(t -> classificationFilter.test(t.getClassificationName()))
                .collect(Collectors.groupingBy(
                        TransactionHistoryResponse::getClassificationName,
                        Collectors.reducing(BigDecimal.ZERO,
                                t -> new BigDecimal(t.getAmount()),
                                BigDecimal::add)))
                .forEach(details::put);
        return details;
    }
}