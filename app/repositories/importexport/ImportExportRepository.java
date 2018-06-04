package repositories.importexport;

import models.Measurement;
import models.Project;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface ImportExportRepository {
    CompletableFuture<Set<Project>> getRelatedProjects(final List<Measurement> measurements);
    CompletableFuture<Void> importData(final List<Project> projects);
}
