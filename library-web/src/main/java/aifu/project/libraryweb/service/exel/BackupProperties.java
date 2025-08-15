package aifu.project.libraryweb.service.exel;

import aifu.project.common_domain.entity.History;
import aifu.project.libraryweb.service.exel.ExcelBackupExporter;
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
