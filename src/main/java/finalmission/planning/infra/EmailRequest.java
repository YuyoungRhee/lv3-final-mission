package finalmission.planning.infra;

public record EmailRequest(
        String From,
        String To,
        String Subject,
        String HtmlBody,
        String MessageStream
) {
}
