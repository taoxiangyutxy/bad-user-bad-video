package com.ttt.one.waiguagg.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class GGFileUtil {
    public static void deleteFilesByName(String fileName) {
        Path projectPath = Paths.get("").toAbsolutePath(); // 当前项目路径

        try (Stream<Path> paths = Files.walk(projectPath)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().contains(fileName))
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                            System.out.println("已删除文件: " + file.toString());
                        } catch (IOException e) {
                            System.err.println("删除文件失败: " + file.toString() + ", 错误: " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.err.println("遍历文件时出错: " + e.getMessage());
        }
    }
}
