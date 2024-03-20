package org.dissan.api;


import org.dissan.controller.BugRetrieverController;
import org.dissan.controller.BugVersionController;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class AnalyzerFactory {

    private AnalyzerFactory(){}

    public static @NotNull BugRetriever getBugRetriever() throws IOException {
            return new BugRetrieverController();
    }

    public static @NotNull BugVersion getBugVersion(){
            return new BugVersionController();
    }
}
