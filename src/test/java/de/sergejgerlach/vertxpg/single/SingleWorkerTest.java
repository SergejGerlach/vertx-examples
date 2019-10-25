package de.sergejgerlach.vertxpg.single;

import io.reactivex.Single;
import org.junit.Test;

public class SingleWorkerTest {

    @Test
    public void process() throws InterruptedException {
        Single<String> process = SingleWorker.process();
        process.subscribe(s -> System.out.println("s = " + s));
        //Thread.sleep(100);
        process.subscribe(s -> System.out.println("s = " + s));
    }
}
