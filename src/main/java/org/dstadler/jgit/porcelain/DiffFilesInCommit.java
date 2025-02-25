package org.dstadler.jgit.porcelain;

/*
   Copyright 2013, 2014 Dominik Stadler

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.dstadler.jgit.helper.CookbookHelper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

/**
 * Simple snippet which shows how to retrieve the diffs
 * between two commits
 */
public class DiffFilesInCommit {

    public static void main(String[] args) throws Exception {
        try (Repository repository = CookbookHelper.openJGitCookbookRepository()) {
            try (Git git = new Git(repository)) {

                // compare older commit with the newer one, showing an addition
                // and 2 changes
                System.out.println("START");
                //listDiff(repository, git,
                //        "3cc51d5cfd1dc3e890f9d6ded4698cb0d22e650e",
                //        "19536fe5765ee79489265927a97cb0e19bb93e70");


                // also the diffing the reverse works and now shows a delete
                // instead of the added file
                //listDiff(repository, git, "19536fe5765ee79489265927a97cb0e19bb93e70", "3cc51d5cfd1dc3e890f9d6ded4698cb0d22e650e");

                // to compare against the "previous" commit, you can use
                // the caret-notation
                listDiff(repository, git, "285db581ff0d53da89ee61a1bf236cbfeae7cb1d^", "285db581ff0d53da89ee61a1bf236cbfeae7cb1d");
                System.out.println("END");
            }
        }
    }

    private static void listDiff(Repository repository, Git git, String oldCommit, String newCommit) throws Exception {
        /*final List<DiffEntry> diffs = git.diff()
                .setOldTree(prepareTreeParser(repository, oldCommit))
                .setNewTree(prepareTreeParser(repository, newCommit))
                .call();

        //System.out.println("Found: " + diffs.size() + " differences");
        FileOutputStream stdout = new FileOutputStream(FileDescriptor.out);

        try (DiffFormatter diffFormatter = new DiffFormatter(stdout)){
            diffFormatter.setRepository(repository);
            for (DiffEntry diff : diffs) {
                diffFormatter.format(diffFormatter.toFileHeader(diff));
                //System.out.println("Diff: " + diff.getChangeType() + ": " +
                //        (diff.getOldPath().equals(diff.getNewPath()) ? diff.getNewPath() : diff.getOldPath() + " -> " + diff.getNewPath()));
            }
        }*/


        RevCommit headCommit = getHeadCommit(repository);
        RevCommit diffWith = headCommit.getParent(0);
        FileOutputStream stdout = new FileOutputStream(FileDescriptor.out);
        try (DiffFormatter diffFormatter = new DiffFormatter(stdout)) {
            diffFormatter.setRepository(repository);
            for (DiffEntry entry : diffFormatter.scan(diffWith, headCommit)) {
                diffFormatter.format(diffFormatter.toFileHeader(entry));
                //System.out.println("ENDOFLINE");
            }
        }
    }
    private static RevCommit getHeadCommit(Repository repository) throws Exception {
        try (Git git = new Git(repository)) {
            Iterable<RevCommit> history = git.log().setMaxCount(1).call();
            return history.iterator().next();
        }
    }

    private static void _get_modified_files(RevCommit commit){
        DiffEntry diff_index;

        if(commit.getParentCount() == 1){

        }
    }

    private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        //noinspection Duplicates
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve(objectId));
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }

            walk.dispose();

            return treeParser;
        }
    }
}
