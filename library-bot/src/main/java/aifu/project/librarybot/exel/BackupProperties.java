package aifu.project.librarybot.exel;

import aifu.project.common_domain.entity.History;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BackupProperties {
    @Value("${backup.path}")
    private String backupPath;

    public void backup(List<History> historyList) {
        ExcelBackupExporter.exportHistoryExcel(historyList, backupPath);
    }
}
