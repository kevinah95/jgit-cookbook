package org.dstadler.jgit.api;

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

import java.io.IOException;
import java.util.List;

import org.dstadler.jgit.helper.CookbookHelper;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

/**
 * Snippet which shows how to use RevWalk and TreeWalk to read the contents
 * of a specific file from a specific commit.
 *
 * @author dominik.stadler at gmx.at
 */
public class ReadFileFromCommit {

    public static void main(String[] args) throws IOException {
        try (Repository repository = CookbookHelper.openJGitCookbookRepository()) {
            // find the HEAD
            ObjectId lastCommitId = repository.resolve(Constants.HEAD);

            // a RevWalk allows to walk over commits based on some filtering that is defined
            try (RevWalk revWalk = new RevWalk(repository)) {
                revWalk.sort( RevSort.COMMIT_TIME_DESC, true );
                revWalk.sort( RevSort.REVERSE , true );
                RevCommit initialCommit = revWalk.next();

                RevCommit commit = revWalk.parseCommit(lastCommitId);
                // and using commit's tree find the path
                RevTree tree = commit.getTree();
                System.out.println("Having tree: " + tree);
                ObjectId oldTree = initialCommit.getTree();

                // now try to find a specific file
                try (TreeWalk treeWalk = new TreeWalk(repository)) {
                    treeWalk.addTree(tree);
                    treeWalk.setRecursive(true);
                    /*treeWalk.setFilter(PathFilter.create("README.md"));
                    if (!treeWalk.next()) {
                        throw new IllegalStateException("Did not find expected file 'README.md'");
                    }*/

                    treeWalk.addTree(oldTree);

                    final RenameDetector rd = new RenameDetector(repository);
                    rd.setRenameScore(80);
                    rd.addAll(DiffEntry.scan(treeWalk));

                    for (DiffEntry diff : rd.compute(treeWalk.getObjectReader(), null)) {
                        DiffEntry.ChangeType changeType = diff.getChangeType();
                        String oldPath = diff.getOldPath();
                        String newPath = diff.getNewPath();
                        if (changeType != DiffEntry.ChangeType.ADD) {
                            //if (isJavafile(oldPath)) {
                            //    javaFilesBefore.add(oldPath);
                            //}
                        }
                        if (changeType != DiffEntry.ChangeType.DELETE) {
                            //if (isJavafile(newPath)) {
                            //    javaFilesCurrent.add(newPath);
                            //}
                        }
                        if (changeType == DiffEntry.ChangeType.RENAME && diff.getScore() >= rd.getRenameScore()) {
                            //if (isJavafile(oldPath) && isJavafile(newPath)) {
                            //    renamedFilesHint.put(oldPath, newPath);
                            //}
                        }
                    }

                    /*while(treeWalk.next()) {
                        System.out.println("===" + treeWalk.getPathString());
                        ObjectId objectId = treeWalk.getObjectId(0);
                        ObjectLoader loader = repository.open(objectId);

                        // and then one can the loader to read the file
                        loader.copyTo(System.out);
                    }*/


                }

                revWalk.dispose();
            }
        }
    }
}
