/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.common.scheduler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public abstract class AbstractScheduler {

    private final ScheduledThreadPoolExecutor scheduler;
    private final CustomExecutor schedulerWorkerPool;
    private final ForkJoinPool worker;

    public AbstractScheduler() {
        this.scheduler = new ScheduledThreadPoolExecutor(1, new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("kingdomcraft-scheduler")
                .build()
        );
        this.scheduler.setRemoveOnCancelPolicy(true);
        this.schedulerWorkerPool = new CustomExecutor(Executors.newCachedThreadPool(new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("kingdomcraft-schedule-worker-%d")
                .build()
        ));
        this.worker = new ForkJoinPool(32, ForkJoinPool.defaultForkJoinWorkerThreadFactory, (t, e) -> e.printStackTrace(), false);
    }

    public abstract Executor sync();

    public Executor async() {
        return this.worker;
    }

    public final void executeAsync(Runnable task) {
        async().execute(task);
    }

    public final void executeSync(Runnable task) {
        sync().execute(task);
    }

    public SchedulerTask asyncLater(Runnable task, long delay, TimeUnit unit) {
        ScheduledFuture<?> future = this.scheduler.schedule(() -> this.schedulerWorkerPool.execute(task), delay, unit);
        return () -> future.cancel(false);
    }

    public SchedulerTask asyncRepeating(Runnable task, long interval, TimeUnit unit) {
        ScheduledFuture<?> future = this.scheduler.scheduleAtFixedRate(() -> this.schedulerWorkerPool.execute(task), interval, interval, unit);
        return () -> future.cancel(false);
    }

    public SchedulerTask syncLater(Runnable task, long delay, TimeUnit unit) {
        ScheduledFuture<?> future = this.scheduler.schedule(() -> this.sync().execute(task), delay, unit);
        return () -> future.cancel(false);
    }

    public SchedulerTask syncRepeating(Runnable task, long interval, TimeUnit unit) {
        ScheduledFuture<?> future = this.scheduler.scheduleAtFixedRate(() -> this.sync().execute(task), interval, interval, unit);
        return () -> future.cancel(false);
    }

    private static final class CustomExecutor implements Executor {
        private final ExecutorService delegate;

        private CustomExecutor(ExecutorService delegate) {
            this.delegate = delegate;
        }

        @Override
        public void execute(Runnable command) {
            this.delegate.execute(new CustomRunnable(command));
        }
    }

    private static final class CustomRunnable implements Runnable {
        private final Runnable delegate;

        private CustomRunnable(Runnable delegate) {
            this.delegate = delegate;
        }

        @Override
        public void run() {
            try {
                this.delegate.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public <T> CompletableFuture<T> makeAsyncFuture(Callable<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.call();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, async());
    }

    public CompletableFuture<Void> makeAsyncFuture(Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, async());
    }

}
