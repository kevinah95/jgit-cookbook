package org.dstadler.jgit.models;

import org.javatuples.Pair;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModifiedFile {

    public enum ChangeType {
        /** Add a new file to the project */
        ADD,

        /** Modify an existing file in the project (content and/or mode) */
        MODIFY,

        /** Delete an existing file from the project */
        DELETE,

        /** Rename an existing file to a new location */
        RENAME,

        /** Copy an existing file to a new location, keeping the original */
        COPY,
        /** Unknown */
        UNKNOWN;
    }

    private Path _old_path;

    private Path _new_path;

    public ChangeType changeType;

    public String diff;

    private String content;

    private String content_before;

    private int _nloc;

    private int _complexity;

    private int _token_count;



    public ModifiedFile(String old_path, String new_path, ChangeType change_type, Map<String, String> diff_and_sc) {
        this._old_path = old_path.isEmpty() ? null : Paths.get(old_path);
        this._new_path = new_path.isEmpty() ? null : Paths.get(new_path);
        this.changeType = change_type;
        this.diff = diff_and_sc.get("diff");
        this.content = diff_and_sc.get("content");
        this.content_before = diff_and_sc.get("content_before");

        //self._nloc = None
        this._nloc = 0;
        //self._complexity = None
        this._complexity = 0;
        //self._token_count = None
        this._token_count = 0;
        //self._function_list: List[Method] = []
        //self._function_list_before: List[Method] = []
    }

    public int AddedLines(){
        int added_lines = 0;

        for(String line : this.diff.replace("\\r","").split("\\n")){
            if(line.startsWith("+") && !line.startsWith("+++")){
                added_lines += 1;
            }
        }

        return added_lines;
    }

    public int DeletedLines(){
        int deleted_lines = 0;
        for(String line : this.diff.replace("\\r","").split("\\n")){
            if(line.startsWith("-") && !line.startsWith("---")){
                deleted_lines += 1;
            }
        }
        return deleted_lines;
    }

    public String get_old_path() {

        return _old_path != null ? _old_path.toString() : null;
    }

    public String get_new_path() {

        return _new_path != null ? _new_path.toString() : null;
    }

    public Map<String, ArrayList<Pair<Integer, String>>> DiffParsed(){
        String[] lines = this.diff.split("\n");

        Map<String, ArrayList<Pair<Integer, String>>> modified_lines = new HashMap<>();
        modified_lines.put("added", new ArrayList<>());
        modified_lines.put("deleted", new ArrayList<>());

        int count_deletions = 0;
        int count_additions = 0;
        for(String line : lines){
            line = line.replaceAll("\\s+$", "");
            count_deletions += 1;
            count_additions += 1;
            if( line.startsWith("@@")){
                count_deletions = _GetLineNumbers(line, true);
                count_additions = _GetLineNumbers(line, false);
            }

            if(line.startsWith("-")){
                modified_lines.get("deleted").add(new Pair<>(count_deletions, line.substring(1)));
                count_additions -= 1;
            }

            if(line.startsWith("+")){
                modified_lines.get("added").add(new Pair<>(count_additions, line.substring(1)));
                count_deletions -= 1;
            }

            if(line == "\\ No newline at end of file"){
                count_deletions -= 1;
                count_additions -= 1;
            }
        }
        return modified_lines;
    }

    private int _GetLineNumbers(String line, boolean returnDeleted){
        String[] token = line.split(" ");
        String numbers_old_file = token[1];
        String numbers_new_file = token[2];
        if(returnDeleted){
            int delete_line_number = (Integer.parseInt(numbers_old_file.split(",")[0].replace("-", "")) - 1);

            return delete_line_number;
        }

        int additions_line_number = Integer.parseInt(numbers_new_file.split(",")[0]) - 1;
        return additions_line_number;
    }
}
