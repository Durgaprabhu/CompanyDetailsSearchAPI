package org.durgaprabhu.interview;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.recording.RecordSpec;
import com.github.tomakehurst.wiremock.recording.RecordingStatus;
import org.durgaprabhu.interview.config.CompanyDetailsApplicationWireMockConfig;
import org.durgaprabhu.interview.config.CompanyDetailsApplicationWireMockProxy;
import org.durgaprabhu.interview.controller.CompanySearchDetailsController;
import org.durgaprabhu.interview.model.CompanySearchRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(value = "integration")
@SpringBootTest(classes = CompanyDetailsSearchEngineAPI.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class CompanyDetailsSearchEngineIT {

    private final static Logger logger = LoggerFactory.getLogger(CompanySearchDetailsController.class);
    public static final String SEARCH_PATH = "/companies/v1/search";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyDetailsApplicationWireMockConfig companyDetailsApplicationWireMockConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final List<WireMockServer> wireMockServers = new ArrayList<>();

    Function<CompanyDetailsApplicationWireMockProxy, WireMockServer> getMockServer =
            getCompanyDetailsApplicationWireMockServerFunction();

    @ParameterizedTest
    @CsvSource({"08805866,true", "10309351,false"})
    public void readCompanyDetailsWithCompanyNumberAndIsActiveFlagTrueFromStub(String companyNumber, String status) throws Exception {
        String expectedResponseFile = String.format("trunarrative/expected-response-%s.json",companyNumber);
        CompanySearchRequest companySearchRequest = new CompanySearchRequest();
        companySearchRequest.setCompanyNumber(companyNumber);

        String actualCompanySearchResponse = mockMvc.perform(post(SEARCH_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJsonString(companySearchRequest)).queryParam("status",status)
                )
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String expectedCompanySearchResponse = getExpectedResponse(expectedResponseFile);
        JSONAssert.assertEquals(expectedCompanySearchResponse, actualCompanySearchResponse, JSONCompareMode.LENIENT);
    }

    @ParameterizedTest
    @CsvSource({"03790585,true", "08805906,false"})
    public void readCompanyDetailsWithCompanyNumberAndIsActiveFlagTrueByCallingTrunArrativeAPI(String companyNumber, String status) throws Exception {
        String expectedResponseFile = String.format("trunarrative/expected-response-%s.json",companyNumber);
        CompanySearchRequest companySearchRequest = new CompanySearchRequest();
        companySearchRequest.setCompanyNumber(companyNumber);
        startStubbingTrunArrativeAPIResponse();

        String actualCompanySearchResponse = mockMvc.perform(post(SEARCH_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJsonString(companySearchRequest)).queryParam("status",status)
                )
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Path tempStubFilePath = createStubFileForTrunArrative(expectedResponseFile, actualCompanySearchResponse);
        String expectedCompanySearchResponse = getExpectedResponse(expectedResponseFile);
        JSONAssert.assertEquals(expectedCompanySearchResponse, actualCompanySearchResponse, JSONCompareMode.LENIENT);
        removeTrunArrativeTempStubFile(tempStubFilePath);

        stopStubbingTrunArrativeAPIResponse();
    }

    private void startStubbingTrunArrativeAPIResponse() {
        List<CompanyDetailsApplicationWireMockProxy> proxies = companyDetailsApplicationWireMockConfig.getProxies();
        if (!CollectionUtils.isEmpty(proxies)) {
            proxies.stream().filter(proxy -> "trunarrative".equalsIgnoreCase(proxy.getName())).findFirst().ifPresent(getCompanyDetailsApplicationWireMockProxyConsumer());
        }
    }

    private Consumer<CompanyDetailsApplicationWireMockProxy> getCompanyDetailsApplicationWireMockProxyConsumer() {
        return proxy -> {
            WireMockServer wireMockServer = getMockServer.apply(proxy);
            wireMockServer.start();
            if (proxy.isStubbing()) {
                wireMockServer.startRecording(wireMockServerConfig(proxy.getUrl()));
            }
            wireMockServers.add(wireMockServer);
        };
    }

    private RecordSpec wireMockServerConfig(String stubbingURL) {
        return WireMock.recordSpec()
                .forTarget(stubbingURL)
                .onlyRequestsMatching(RequestPatternBuilder.allRequests())
                .captureHeader("Accept")
                .makeStubsPersistent(true)
                .ignoreRepeatRequests()
                .matchRequestBodyWithEqualToJson(true, true)
                .build();
    }

    private void stopStubbingTrunArrativeAPIResponse() {
        if (!CollectionUtils.isEmpty(wireMockServers)) {
            for (WireMockServer server : wireMockServers) {
                if (server.getRecordingStatus().getStatus().equals(RecordingStatus.Recording)) {
                    server.stopRecording();
                }
                server.stop();
            }
        }
    }

    private Path createStubFileForTrunArrative(String expectedResponseFile, String result) throws Exception {
        String[] segments = expectedResponseFile.split("/");
        String stubFileName = segments[segments.length - 1];
        File file = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource(".")).getFile() + stubFileName);
        try (Writer writer =
                     new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            Object json = objectMapper.readValue(result, Object.class);
            writer.write(
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
        }
       if (isStubbingEnabled()) {
              Path copied = Paths.get("src/test/resources/trunarrative/" + file.toPath().getFileName());
              Files.copy(Paths.get(file.getPath()), copied, StandardCopyOption.REPLACE_EXISTING);
       }
        return file.toPath();
    }

    private boolean isStubbingEnabled() {
        List<CompanyDetailsApplicationWireMockProxy> proxies = companyDetailsApplicationWireMockConfig.getProxies();
        if (!CollectionUtils.isEmpty(proxies)) {
            return proxies.stream().filter(x -> "trunarrative".equalsIgnoreCase(x.getName())).anyMatch(CompanyDetailsApplicationWireMockProxy::isStubbing);
        }
        return false;
    }

    private void removeTrunArrativeTempStubFile(Path stubFilePath) throws IOException {
        boolean result = Files.deleteIfExists(stubFilePath);
        if (result) {
            logger.info("File Successfully deleted");
        } else {
            logger.error(" File delete filed :{}", stubFilePath.getFileName());
        }
    }

    private String getExpectedResponse(String expectedResponseFile) throws IOException {
        Path resourceDirectory = Paths.get("src", "test", "resources");
        Path pathToFile = resourceDirectory.resolve(expectedResponseFile);
        if (!pathToFile.toFile().exists()) {
            throw new IllegalArgumentException(
                    "Expected file not present " + pathToFile.getFileName());
        }
        Object expectedCompanySearchResult = objectMapper.readValue(pathToFile.toFile(), Object.class);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedCompanySearchResult);
    }

    private String objectToJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Function<CompanyDetailsApplicationWireMockProxy, WireMockServer> getCompanyDetailsApplicationWireMockServerFunction() {
        return (CompanyDetailsApplicationWireMockProxy proxy) ->
                new WireMockServer(WireMockConfiguration.options()
                        .port(proxy.getPort()).notifier(new ConsoleNotifier(true)));
    }
}
