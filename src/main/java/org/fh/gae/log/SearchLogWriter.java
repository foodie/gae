package org.fh.gae.log;

import lombok.extern.slf4j.Slf4j;
import org.fh.gae.config.GaeServerProps;
import org.fh.gae.query.session.ThreadCtx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class SearchLogWriter {
    @Autowired
    private GaeServerProps props;

    public void writeLog(String slotId) throws IOException {
        SearchLog searchLog = ThreadCtx.getSearchLogMap().get(slotId);
        if (log.isDebugEnabled()) {
            log.debug("search_log\t{}", searchLog.toString());
        }

        writeLog(searchLog);
    }

    public synchronized void writeLog(SerializableLog serializableLog) throws IOException {
        File file = getFile();

        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            BufferedOutputStream out = new BufferedOutputStream(fos);

            out.write(serializableLog.serializeLog());
            out.write('\n');

            out.flush();

        } catch (IOException ex) {
            throw ex;
        }

    }

    private File getFile() throws IOException {
        File f = new File(props.getSearchLog());
        if (!f.exists()) {
            f.createNewFile();
        }

        return f;
    }
}
