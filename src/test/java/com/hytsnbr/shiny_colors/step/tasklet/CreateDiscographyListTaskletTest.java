package com.hytsnbr.shiny_colors.step.tasklet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.hytsnbr.shiny_colors.config.BatchConfig;
import com.hytsnbr.shiny_colors.service.FileOperator;
import com.hytsnbr.shiny_colors.util.JsoupUtil;

@SpringBatchTest
@SpringBootTest(classes = {BatchConfig.class, CreateDiscographyListTasklet.class})
@ActiveProfiles("test")
class CreateDiscographyListTaskletTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @MockBean
    private FileOperator fileOperator; // JSON出力をモック

    private MockedStatic<JsoupUtil> mockedJsoupUtil;

    @AfterEach
    void tearDown() {
        if (mockedJsoupUtil != null) {
            mockedJsoupUtil.close();
        }
    }

    @Test
    void testCreateDiscographyListStep_success() throws Exception {
        // ======= モックHTMLを準備 =======
        String html = """
                <div id="discographys">
                    <div class="CD_area">
                        <div class="dsc_box">
                            <h4><span>■シャニマスCDシリーズ</span></h4>
                            <a href="https://example.com/cd1">CD1</a>
                            <a href="https://example.com/cd2">CD2</a>
                        </div>
                    </div>
                </div>
                """;
        Document mockDoc = Parser.parse(html, "UTF-8");

        // ======= JsoupUtil.connectJsoup をモック =======
        mockedJsoupUtil = mockStatic(JsoupUtil.class);
        mockedJsoupUtil.when(() -> JsoupUtil.connectJsoup(any(), any()))
                       .thenReturn(mockDoc);

        // ======= FileOperator の出力を検証可能にする =======
        doNothing().when(fileOperator).outputToJsonFile(any(), any());

        // ======= Step実行 =======
        StepExecution stepExecution = jobLauncherTestUtils.launchStep("create_discography_list_step");

        // ======= 検証 =======
        assertThat(stepExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        // 出力呼び出し確認
        verify(fileOperator, times(1)).outputToJsonFile(any(), eq("DiscographyList.json"));
    }
}
