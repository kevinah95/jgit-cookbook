package org.dstadler.jgit.api;

/*
   Copyright 2016 Dominik Stadler

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

import org.dstadler.jgit.helper.CookbookHelper;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Simple snippet which shows how to use RevWalk to iterate over all commits
 * across all branches/tags/remotes in the given repository
 *
 * See the original discussion at http://stackoverflow.com/a/40803945/411846
 */
public class WalkAllCommits {

    public static void main(String[] args) throws IOException {
        try (Repository repository = CookbookHelper.openJGitCookbookRepository()) {
            // get a list of all known heads, tags, remotes, ...
            Collection<Ref> allRefs = repository.getAllRefs().values();

            // a RevWalk allows to walk over commits based on some filtering that is defined
            try (RevWalk revWalk = new RevWalk( repository )) {
                for( Ref ref : allRefs ) {
                    revWalk.markStart( revWalk.parseCommit( ref.getObjectId() ));
                }
                System.out.println("Walking all commits starting with " + allRefs.size() + " refs: " + allRefs);
                int count = 0;
                for( RevCommit commit : revWalk ) {
                    System.out.println("Commit: " + commit);
                    // get commit author
                    System.out.println("Author.Name: " + commit.getAuthorIdent().getName());
                    System.out.println("Author.Email: " + commit.getAuthorIdent().getEmailAddress());
                    //get file-paths
                    List<String> paths = readElementsAt(repository, commit);
                    System.out.println("Files of the author: " + paths);

                    count++;
                }



                System.out.println("Had " + count + " commits");
            }
        }
    }

    //From ListFilesOfCommit.java
    // TODO: To calculate renamed files use RenameDetector
    public static List<String> readElementsAt(Repository repository, RevCommit commit) throws IOException {
        // and using commit's tree find the path
        RevTree tree = commit.getTree();
        //System.out.println("Having tree: " + tree);

        List<String> items = new ArrayList<>();

        // now try to find a specific file
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            //treeWalk.setPostOrderTraversal(false);

            while(treeWalk.next()) {
                items.add(treeWalk.getPathString());
            }

        }

        return items;
    }
}
