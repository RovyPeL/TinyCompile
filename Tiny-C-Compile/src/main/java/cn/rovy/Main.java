package cn.rovy;

import cn.rovy.ds.GlobalVar;
import cn.rovy.intermediate.Intermediate;
import cn.rovy.token.TokenAnalysis;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {
        String test;
        TokenAnalysis tokenAnalysis = new TokenAnalysis();
        Intermediate intermediate = new Intermediate();

        test = Files.readString(Path.of("src/main/java/cn/rovy/test/test.c"));
        tokenAnalysis.analysis(test);
        //intermediate.generate();

        //System.out.println(GlobalVar.getInstance().getTokens());

        Files.writeString(Path.of("src/main/java/cn/rovy/out/tokens.txt"),GlobalVar.getInstance().showTokenAndNum());
        //Files.writeString(Path.of("src/main/java/cn/rovy/out/fourItem.txt"),GlobalVar.getInstance().getFourItemList().toString());
    }
}