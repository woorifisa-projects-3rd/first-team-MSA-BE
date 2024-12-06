package com.example.Finance.service;

import com.example.Finance.error.CustomException;
import com.example.Finance.error.ErrorCode;
import com.itextpdf.text.pdf.BaseFont;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

@Service
@Slf4j
public class PdfService {

    public byte[] convertHtmlToPdf(String html) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ITextRenderer renderer = new ITextRenderer();

            // 폰트 설정
            String fontPath = "/app/fonts/NanumGothic-Regular.ttf";

            // 폰트 등록
            renderer.getFontResolver().addFont(
                    fontPath,
                    BaseFont.IDENTITY_H,
                    BaseFont.NOT_EMBEDDED
            );

            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(baos, true);
            baos.flush();

            byte[] pdfContent = baos.toByteArray();
            log.info("PDF 생성 완료: {} bytes", pdfContent.length);
            return pdfContent;

        } catch (Exception e) {
            log.error("PDF 변환 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.PDF_CREATE_ERROR);
        }
    }
}