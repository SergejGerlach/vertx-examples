package de.sergejgerlach.vertxpg.single;

import io.reactivex.Single;

import java.util.Random;

public class SingleWorker {

    public static final Single<String> process() {
        return Single.create(emitter -> {
            String rc = Thread.currentThread().getName() + " - " + System.currentTimeMillis() + " - " + new Random().nextInt();
            System.out.println("produce a string : " + rc);
            emitter.onSuccess(rc);
        });
    }
}
