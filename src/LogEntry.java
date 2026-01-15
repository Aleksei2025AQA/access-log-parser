import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
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

    private static final Pattern LOG_PATTERN = Pattern.compile(
            "^([\\d.]+) \\S+ \\S+ \\[(.+?)\\] \"(\\w+) (.+?) HTTP/\\d\\.\\d\" (\\d+) (\\d+) \"([^\"]*)\" \"([^\"]*)\"$"
    );

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    public LogEntry(String logLine) {
        Matcher matcher = LOG_PATTERN.matcher(logLine);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Неверный формат строки лога: " + logLine);
        }

        this.ipAddr = matcher.group(1);
        this.time = LocalDateTime.parse(matcher.group(2), DATE_FORMATTER);
        this.method = HttpMethod.fromString(matcher.group(3));
        this.path = matcher.group(4);
        this.responseCode = Integer.parseInt(matcher.group(5));
        this.responseSize = Integer.parseInt(matcher.group(6));
        this.referer = matcher.group(7);
        this.userAgent = new UserAgent(matcher.group(8));
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
    public String toString() {
        return "LogEntry{ipAddr='" + ipAddr +
                "', time=" + time +
                ", method=" + method +
                ", path='" + path +
                "', responseCode=" + responseCode +
                ", responseSize=" + responseSize +
                ", referer='" + referer +
                "', userAgent=" + userAgent + "}";
    }
}