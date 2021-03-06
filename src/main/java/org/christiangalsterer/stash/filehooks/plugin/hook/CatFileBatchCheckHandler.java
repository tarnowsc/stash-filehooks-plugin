package org.christiangalsterer.stash.filehooks.plugin.hook;

import com.atlassian.bitbucket.io.LineReader;
import com.atlassian.bitbucket.io.LineReaderOutputHandler;
import com.atlassian.bitbucket.scm.CommandOutputHandler;
import com.atlassian.fugue.Pair;
import com.atlassian.utils.process.ProcessException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CatFileBatchCheckHandler extends LineReaderOutputHandler
    implements CommandOutputHandler<List<Pair<String, Long>>> {

    private static final String UTF_8 = "UTF-8";
    private static final Pattern pattern = Pattern.compile("^([0-9a-f]{40})\\sblob\\s(\\d+)$");

    private final List<Pair<String, Long>> values = new ArrayList<>();

    public CatFileBatchCheckHandler() {
        super(UTF_8);
    }

    @Override
    public List<Pair<String, Long>> getOutput() {
        return values;
    }

    @Override
    public void complete() {
        try {
            super.complete();
        } catch (ProcessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void processReader(LineReader reader) throws IOException {
        String line;
        while ((line = resetWatchdogAndReadLine(reader)) != null) {
            final Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                values.add(Pair.pair(matcher.group(1), Long.parseLong(matcher.group(2))));
            }
        }
    }
}
