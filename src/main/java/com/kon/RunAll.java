package com.kon;
/* async-programming
 * @created 10/31/2022
 * @author Konstantin Staykov
 */

import java.util.concurrent.ExecutionException;

public class RunAll {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        A_RunSynchronousTasks.run();
        B_RunExecutorTasks.run();
        C_RunAsyncTasks.run();
    }
}
