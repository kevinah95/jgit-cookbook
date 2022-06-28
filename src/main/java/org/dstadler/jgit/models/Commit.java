package org.dstadler.jgit.models;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Commit {

    private RevCommit commitObject;

    private ArrayList<ModifiedFile> modifiedFiles;

    public Commit(RevCommit commit) {
        this.commitObject = commit;
    }

    public String Hash(){
        return this.commitObject.getName();
    }

    public Developer Author(){
        PersonIdent author = this.commitObject.getAuthorIdent();
        return new Developer(author.getName(), author.getEmailAddress());
    }

    public Developer Commiter(){
        PersonIdent commiter = this.commitObject.getCommitterIdent();
        return new Developer(commiter.getName(), commiter.getEmailAddress());
    }

    public String Message(){
        return commitObject.getFullMessage();
    }

    public ArrayList<String> Parents(){
        ArrayList<String> parents = new ArrayList<>();
        for(RevCommit parent : this.commitObject.getParents()){
            parents.add(parent.getName());
        }
        return parents;
    }

    public boolean Merge(){
        return this.commitObject.getParentCount() > 1;
    }

    public ArrayList<ModifiedFile> ModifiedFiles(){
        if(this.modifiedFiles == null){
            this.modifiedFiles =_GetModifiedFiles();
        }

        assert this.modifiedFiles != null;
        return this.modifiedFiles;
    }

    private ArrayList<ModifiedFile> _GetModifiedFiles(){
        throw new UnsupportedOperationException();
    }

    //TODO: Find the way to pass repository variable
    private void _Diff(RevCommit oldCommit, RevCommit newCommit) throws IOException {
        FileOutputStream stdout = new FileOutputStream(FileDescriptor.out);
        try (DiffFormatter diffFormatter = new DiffFormatter(stdout)) {
            //diffFormatter.setRepository(repository);
            for (DiffEntry entry : diffFormatter.scan(oldCommit.getTree(), newCommit.getTree())) {
                diffFormatter.format(diffFormatter.toFileHeader(entry));
            }
        }
    }
}
