package org.dstadler.jgit.models;

import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Data
public class Repository {
    private String path_to_repo;
    private Path _tempDir;
    public Repository(String path_to_repo) {
        this.path_to_repo = path_to_repo;
    }

    private boolean _IsRemote(String repo){
        return repo.startsWith("git@") || repo.startsWith("https://") || repo.startsWith("http://");
    }

    private String _GetRepoNameFromURL(String url) throws Exception {
        int last_slash_index = url.lastIndexOf("/");
        int last_suffix_index = url.lastIndexOf(".git");
        if (last_suffix_index < 0){
            last_suffix_index = url.length();
        }
        if(last_slash_index < 0 || last_suffix_index <= last_slash_index){
            throw new Exception("Badly formatted url");
        }

        return url.substring(last_slash_index + 1, last_suffix_index);

    }

    private String _CloneRemoteRepo(String tempFolder, String repo) throws Exception {
        Path repo_folder = Paths.get(tempFolder, _GetRepoNameFromURL(repo));
        if(Files.isDirectory(repo_folder)){
            System.out.println("Reusing folder "+ repo_folder.toAbsolutePath() + " for "+ repo);
        } else{
            System.out.println("Cloning "+ repo + " in temp folder "+repo_folder.toAbsolutePath());
            Git git = Git.cloneRepository()
                    .setURI(repo)
                    .setDirectory(repo_folder.toFile())
                    .setProgressMonitor(new SimpleProgressMonitor())
                    .call();

            git.close();
        }

        return repo_folder.toAbsolutePath().toString();
    }



    private String _CloneFolder() throws IOException {
        _tempDir = Files.createTempDirectory("TruckFactorRepos");
        return _tempDir.toAbsolutePath().toString();
    }

    private org.eclipse.jgit.lib.Repository _PrepRepo(String path_repo) throws Exception {
        String local_path_repo = path_repo;
        if (_IsRemote(path_repo)){
            local_path_repo = _CloneRemoteRepo(_CloneFolder(), path_repo);
        }

        // now open a FileRepositoryBuilder
        return FileRepositoryBuilder.create(new File(local_path_repo, ".git"));
    }

    public Stream<Commit> TraverseCommits() throws Exception {
        try(org.eclipse.jgit.lib.Repository repository = _PrepRepo(this.path_to_repo)){
            try (Git git = new Git(repository)) {
                Iterable<RevCommit> commits = git.log().all().call();
                return StreamSupport.stream(commits.spliterator(), false)
                        .map(commit -> new Commit(commit))
                        .onClose(()-> {
                            try {
                                FileUtils.deleteDirectory(repository.getDirectory().getParentFile().getParentFile());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });

            }
        }
    }


    private static class SimpleProgressMonitor implements ProgressMonitor {
        @Override
        public void start(int totalTasks) {
            System.out.println("Starting work on " + totalTasks + " tasks");
        }

        @Override
        public void beginTask(String title, int totalWork) {
            System.out.println("Start " + title + ": " + totalWork);
        }

        @Override
        public void update(int completed) {
            System.out.print(completed + "-");
        }

        @Override
        public void endTask() {
            System.out.println("Done");
        }

        @Override
        public boolean isCancelled() {
            return false;
        }
    }
}
