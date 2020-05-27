package com.sandklef.compliance.domain;

import com.sandklef.compliance.utils.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sandklef.compliance.utils.Log.indents;

public class ComplianceAnswer {


    private static final String LOG = ComplianceAnswer.class.getCanonicalName() ;
    // License.spdx
    private Map<String, Map<Component, List<ComplianceAnswer>>> answers;
    private ListType color;

    @Override
    public String toString() {
        return " {" +
                   answers +
                '}';
    }

    public ListType color() {
        return color;
    }

    public void color(ListType color) {
        this.color = color;
    }







    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        if (answers.entrySet().size() > 0) {
            for (Map.Entry<String, Map<Component, List<ComplianceAnswer>>> entry : answers.entrySet()) {

                //              sb.append(indents(indent));
//                sb.append("[" + answers.entrySet().size() + "]\n");
                String license = entry.getKey();
                Object value = entry.getValue();
                System.out.println("TOSTRING: " + license + "   " + ((Map<Component, List<ComplianceAnswer>>) value).entrySet().size());
                System.out.println("TOSTRING: " + license + "   " + ((Map<Component, List<ComplianceAnswer>>) value).getClass().getCanonicalName());
                if (((Map<Component, List<ComplianceAnswer>>) value).entrySet().size() > 0) {
//                    sb.append(indents(indent));
  //                  sb.append("  | ");
                    //sb.append(key + " ( keyset: \"" + ((Map<Component, List<ComplianceAnswer>>) value).keySet().size() + "\")");
                    //sb.append(license);
                    /*
                    if (this.color() != ListType.WHITE_LIST) {
                        sb.append(" (");
                        sb.append(this.color());
                        sb.append(")");
                    }
                    */
                    sb.append("\n");
                    int count=0;
                    for (Map.Entry<Component, List<ComplianceAnswer>> entry2 : ((Map<Component, List<ComplianceAnswer>>) value).entrySet()) {
                        System.out.println("TOSTRING: count: " + count++);
                        System.out.println("TOSTRING: class: " + entry2.getKey().getClass().getCanonicalName());
                        System.out.println("TOSTRING: class: " + entry2.getClass().getCanonicalName());
                        System.out.println("TOSTRING: class: " + entry2.getValue().getClass().getCanonicalName());
                        System.out.println("TOSTRING: class: " + entry2.getKey());
                        System.out.println("TOSTRING: size:  " + entry2.getValue().size());
                        System.out.println("TOSTRING: ");
                        ;

                        sb.append(indents(indent));
                        sb.append("  +-- ");
                        sb.append(entry2.getKey().name());
                        /*    sb.append(" (licenses: ");
                            sb.append(entry2.getKey().licenses().size());
                            sb.append(" deps: ");
                            sb.append(entry2.getKey().dependencies().size());
                            sb.append("   answers:");
                            sb.append(entry2.getValue() == null ? 0 : entry2.getValue().size());
                          */
//                        sb.append(" " + ((Map<Component, List<ComplianceAnswer>>) value).entrySet().size()+ "\n");
                        sb.append(" (" + license + " )");
                        if (entry2.getValue() != null) {
//                                sb.append(" TEST: " + entry2.getValue() + "\n");
                            for (ComplianceAnswer ca : entry2.getValue()) {
                                if (ca != null) {
                                    //sb.append(indents(indent));
//                                        sb.append("    print sub " + ca.answers.size() + "\n");
                                    sb.append(ca.toString(indent + 2));
                                } else {
                                    sb.append("====================================== 1 null\n");
                                }
                            }
                        } else {
                            sb.append("====================================== 2 null\n");
                            //else { sb.append("====================================== null\n"); }
                        }
                    }
                }
            }
        } else {
//            sb.append(indents(indent));
//            sb.append("  OK");
            if (this.color() != ListType.WHITE_LIST) {
                sb.append(" (");
                sb.append(this.color());
                sb.append(")");
            }
            sb.append("\n");

        }
        return sb.toString();
    }

    public int paths(ListType color) {
        int count = 0;
        if (answers.entrySet().size() > 0) {
            for (Map.Entry<String, Map<Component, List<ComplianceAnswer>>> entry : answers.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (((Map<Component, List<ComplianceAnswer>>) value).entrySet().size() > 0) {
                    if (this.color() != ListType.WHITE_LIST) {
                    }
                    for (Map.Entry<Component, List<ComplianceAnswer>> entry2 : ((Map<Component, List<ComplianceAnswer>>) value).entrySet()) {
                        if (entry2.getValue() != null) {
                            for (ComplianceAnswer ca : entry2.getValue()) {
                                if (ca != null) {
                                    count += ca.paths(color);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            count++;
            if (this.color() != ListType.WHITE_LIST) {
            }
        }
        return count;
    }

    public int grayPaths() {
        return paths(ListType.GRAY_LIST);
    }

    public static ComplianceAnswer okAnswerNoDeps() {
        return new ComplianceAnswer();
    }

    public static ComplianceAnswer okAnswerNoDepsGray() {
        return new ComplianceAnswer(ListType.GRAY_LIST);
    }

    public static ComplianceAnswer failAnswer; // initialized to null, keep it that way

    public ComplianceAnswer() {
        answers = new HashMap<>();
        color = ListType.WHITE_LIST;
    }

    public ComplianceAnswer(ListType color) {
        answers = new HashMap<>();
        this.color = color;
    }

    public Map<Component, List<ComplianceAnswer>> licenseMap(License license) {
        return licenseMap(license.spdx());
    }

    public Map<Component, List<ComplianceAnswer>> licenseMap(String spdx) {
        if (answers.get(spdx) == null) {
            answers.put(spdx, new HashMap<>());
        }
        return answers.get(spdx);
    }

    public List<ComplianceAnswer> answers(String spdx, Component component) {
        if (licenseMap(spdx).get(component) == null) {
            licenseMap(spdx).put(component, new ArrayList<>());
        }
        return licenseMap(spdx).get(component);
    }

    public Map<String, Map<Component, List<ComplianceAnswer>>> answers() {
        return answers;
    }
}