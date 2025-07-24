package aifu.project.libraryweb.config;

import java.util.List;

public record ImportStats(
        int successfulImports,
        List<String> errors
) {}