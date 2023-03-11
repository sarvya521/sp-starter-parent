package com.sp.boilerplate.commons.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public class FunctionalReadWriteLock {

  private final Lock readLock;
  private final Lock writeLock;

  public FunctionalReadWriteLock() {
    this(new ReentrantReadWriteLock());
  }

  public FunctionalReadWriteLock(ReadWriteLock lock) {
    readLock = lock.readLock();
    writeLock = lock.writeLock();
  }

  public <T> T read(Supplier<T> block) {
    readLock.lock();
    try {
      return block.get();
    } finally {
      readLock.unlock();
    }
  }

  public void read(Runnable block) {
    readLock.lock();
    try {
      block.run();
    } finally {
      readLock.unlock();
    }
  }

  public <T> T write(Supplier<T> block) {
    writeLock.lock();
    try {
      return block.get();
    } finally {
      writeLock.unlock();
    }
  }

  public void write(Runnable block) {
    writeLock.lock();
    try {
      block.run();
    } finally {
      writeLock.unlock();
    }
  }

}