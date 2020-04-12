package de.derrop.minecraft.proxy.logging;
/*
 * Created by Mc_Ruben on 16.04.2019
 */

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AsyncPrintStream extends PrintStream implements Runnable {

    private Thread worker;
    private BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
    @Setter
    @Getter
    private boolean async = true;

    public AsyncPrintStream(OutputStream out) {
        super(out);
        this.worker = new Thread(this, "AsyncPrintStream-Worker");
        this.worker.start();
    }

    public AsyncPrintStream(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
        this.worker = new Thread(this, "AsyncPrintStream-Worker");
        this.worker.start();
    }

    @Override
    public void print(int i) {
        print(String.valueOf(i));
    }

    @Override
    public void print(char c) {
        print(String.valueOf(c));
    }

    @Override
    public void print(long l) {
        print(String.valueOf(l));
    }

    @Override
    public void print(float f) {
        print(String.valueOf(f));
    }

    @Override
    public void print(char[] s) {
        print(String.valueOf(s));
    }

    @Override
    public void print(double d) {
        print(String.valueOf(d));
    }

    @Override
    public void print(boolean b) {
        print(String.valueOf(b));
    }

    @Override
    public void print(Object obj) {
        print(String.valueOf(obj));
    }

    @Override
    public void println() {
        print(System.lineSeparator());
    }

    @Override
    public void println(int x) {
        println(String.valueOf(x));
    }

    @Override
    public void println(char x) {
        println(String.valueOf(x));
    }

    @Override
    public void println(long x) {
        println(String.valueOf(x));
    }

    @Override
    public void println(float x) {
        println(String.valueOf(x));
    }

    @Override
    public void println(char[] x) {
        println(String.valueOf(x));
    }

    @Override
    public void println(double x) {
        println(String.valueOf(x));
    }

    @Override
    public void println(Object x) {
        println(String.valueOf(x));
    }

    @Override
    public void println(boolean x) {
        println(String.valueOf(x));
    }

    @Override
    public void close() {
        this.worker.interrupt();
        super.close();
    }

    @Override
    public void println(String x) {
        print(x);
        print(System.lineSeparator());
    }

    @Override
    public void print(String s) {
        if (s == null)
            s = "null";
        if (this.async) {
            this.logQueue.offer(s);
        } else {
            super.print(s);
        }
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                if (!async)
                    Thread.sleep(250);
                super.print(this.logQueue.take());
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
