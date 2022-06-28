package org.dstadler.jgit.models;

import junit.framework.TestCase;
import org.javatuples.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class ModifiedFileTest extends TestCase {

    public void testAddedLines() throws IOException {
        // Arrange

        java.nio.file.Path resPath = java.nio.file.Paths.get("/Users/khernandezr/Documents/workshop/kevs/jgit-cookbook/src/test/resources/diff-example.txt");

        String diff = new String(java.nio.file.Files.readAllBytes(resPath), "UTF8");

        Map<String, String> diff_and_sc = new HashMap<String, String>();
        diff_and_sc.put("diff", diff);
        diff_and_sc.put("content", "");
        diff_and_sc.put("content_before", "");


        // Act
        ModifiedFile modifiedFile = new ModifiedFile("", "", ModifiedFile.ChangeType.ADD, diff_and_sc);

        // Assert
        assertThat(modifiedFile.AddedLines()).isEqualTo(41);

    }

    public void testDeletedLines() throws IOException {
        // Arrange

        java.nio.file.Path resPath = java.nio.file.Paths.get("/Users/khernandezr/Documents/workshop/kevs/jgit-cookbook/src/test/resources/diff-example.txt");

        String diff = new String(java.nio.file.Files.readAllBytes(resPath), "UTF8");

        Map<String, String> diff_and_sc = new HashMap<String, String>();
        diff_and_sc.put("diff", diff);
        diff_and_sc.put("content", "");
        diff_and_sc.put("content_before", "");


        // Act
        ModifiedFile modifiedFile = new ModifiedFile("", "", ModifiedFile.ChangeType.ADD, diff_and_sc);

        // Assert
        assertThat(modifiedFile.DeletedLines()).isEqualTo(2);

    }

    public void testDiffParsed() throws IOException {

        // Arrange

        java.nio.file.Path resPath = java.nio.file.Paths.get("/Users/khernandezr/Documents/workshop/kevs/jgit-cookbook/src/test/resources/diff-example-02.txt");

        String diff = new String(java.nio.file.Files.readAllBytes(resPath), "UTF8");

        Map<String, String> diff_and_sc = new HashMap<String, String>();
        diff_and_sc.put("diff", diff);
        diff_and_sc.put("content", "");
        diff_and_sc.put("content_before", "");


        // Act
        ModifiedFile modifiedFile = new ModifiedFile("", "", ModifiedFile.ChangeType.ADD, diff_and_sc);

        // Assert
        Map<String, ArrayList<Pair<Integer, String>>> c = modifiedFile.DiffParsed();

        //assertThat(modifiedFile.DeletedLines()).isEqualTo(2);
    }
}