import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogEntry {
    private final String ipAddr;
    private final LocalDateTime time;
    private final HttpMethod method;
    private final String path;
    private final int responseCode;
    private final int responseSize;
    private final String referer;
    private final UserAgent userAgent;

    private static final String IP_ADDRESS_PATTERN = "([\\d.]+)";
    private static final String IDENTITY_PATTERN = "\\S+";
    private static final String USER_ID_PATTERN = "\\S+";
    private static final String TIMESTAMP_PATTERN = "\\[(.+?)\\]";
    private static final String HTTP_METHOD_PATTERN = "(\\w+)";
    private static final String PATH_PATTERN = "(.+?)";
    private static final String HTTP_VERSION_PATTERN = "HTTP/\\d\\.\\d";
    private static final String RESPONSE_CODE_PATTERN = "(\\d+)";
    private static final String RESPONSE_SIZE_PATTERN = "(\\d+|-)";
    private static final String REFERER_PATTERN = "\"([^\"]*)\"";
    private static final String USER_AGENT_PATTERN = "\"([^\"]*)\"";

    private static final Pattern LOG_PATTERN = Pattern.compile(
            "^" + IP_ADDRESS_PATTERN + " " +
                    IDENTITY_PATTERN + " " +
                    USER_ID_PATTERN + " " +
                    TIMESTAMP_PATTERN + " " +
                    "\"" + HTTP_METHOD_PATTERN + " " + PATH_PATTERN + " " + HTTP_VERSION_PATTERN + "\" " +
                    RESPONSE_CODE_PATTERN + " " +
                    RESPONSE_SIZE_PATTERN + " " +
                    REFERER_PATTERN + " " +
                    USER_AGENT_PATTERN + "$"
    );

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    public LogEntry(String logLine) {
        Matcher matcher = LOG_PATTERN.matcher(logLine);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Неверный формат строки лога: " + logLine);
        }

        try {
            this.ipAddr = matcher.group(1);
            this.time = LocalDateTime.parse(matcher.group(2), DATE_FORMATTER);
            this.method = HttpMethod.fromString(matcher.group(3));
            this.path = matcher.group(4);
            this.responseCode = Integer.parseInt(matcher.group(5));
            this.responseSize = parseResponseSize(matcher.group(6));
            this.referer = matcher.group(7);
            this.userAgent = new UserAgent(matcher.group(8));
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Ошибка парсинга строки лога: " + logLine +
                            ". Причина: " + e.getMessage(),
                    e
            );
        }
    }

    private int parseResponseSize(String sizeStr) {
        if (sizeStr == null || sizeStr.equals("-") || sizeStr.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(sizeStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static Optional<LogEntry> parse(String logLine) {
        try {
            return Optional.of(new LogEntry(logLine));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public String getReferer() {
        return referer;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogEntry logEntry = (LogEntry) o;
        return responseCode == logEntry.responseCode &&
                responseSize == logEntry.responseSize &&
                Objects.equals(ipAddr, logEntry.ipAddr) &&
                Objects.equals(time, logEntry.time) &&
                method == logEntry.method &&
                Objects.equals(path, logEntry.path) &&
                Objects.equals(referer, logEntry.referer) &&
                Objects.equals(userAgent, logEntry.userAgent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipAddr, time, method, path, responseCode, responseSize, referer, userAgent);
    }

    @Override
    public String toString() {
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format(
                "LogEntry{ipAddr='%s', time=%s, method=%s, path='%s', " +
                        "responseCode=%d, responseSize=%d, referer='%s', userAgent=%s}",
                ipAddr,
                time.format(displayFormatter),
                method,
                path,
                responseCode,
                responseSize,
                referer,
                userAgent
        );
    }
}