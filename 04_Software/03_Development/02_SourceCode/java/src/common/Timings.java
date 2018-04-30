/*
 * The MIT License
 *
 * Copyright 2017 Olimpia Popica, Benone Aligica
 *
 * Contact: contact[a(t)]annotate[(d){o}t]zone
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package common;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that offers methods for computing the needed time to run some methods.
 *
 * @author Olimpia Popica
 */
public class Timings {

    private long startTime;     // in ns
    private long endTime;       // in ns
    private long neededTime;    // in ms

    private long startElpasedTime;
    private long stopElpasedTime;

    /**
     * logger instance
     */
    private final Logger log = LoggerFactory.getLogger("common.Timings");

    /**
     * Initialises the timer with the current value.
     */
    public void start() {
        startTime = System.nanoTime();
    }

    /**
     * Initialises the timer with the current value.
     */
    public void startElapsed() {
        startElpasedTime = System.nanoTime();
    }

    /**
     * Computes the elapsed time
     *
     * @param description - the description of the measured task
     */
    public void stopElapsed(String description) {
        stopElpasedTime = System.nanoTime();
        neededTime = (stopElpasedTime - startElpasedTime) / 1000000;
        log.info("{}\r\n{} min, {} sec\r\n{}",
                description,
                (TimeUnit.MILLISECONDS.toMinutes(neededTime) % 60),
                TimeUnit.MILLISECONDS.toSeconds(neededTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(neededTime)),
                description);
    }

    /**
     * Computes the time which passed between when the timer was started and the
     * present time.
     *
     * @param description - the description of the measured task
     */
    public void stop(String description) {
        endTime = System.nanoTime();
        neededTime = (endTime - startTime) / 1000000;
        log.info("{} {} ms, {} h, {} min, {} sec",
                description,
                neededTime,
                TimeUnit.MILLISECONDS.toHours(neededTime),
                (TimeUnit.MILLISECONDS.toMinutes(neededTime) % 60),
                TimeUnit.MILLISECONDS.toSeconds(neededTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(neededTime)));
    }

    /**
     * Computes the time which passed between when the timer was started and the
     * present time.
     */
    public void stop() {
        endTime = System.nanoTime();
        neededTime = (endTime - startTime) / 1000000;
        log.info("{} ms, {} h, {} min, {} sec",
                neededTime,
                TimeUnit.MILLISECONDS.toHours(neededTime),
                (TimeUnit.MILLISECONDS.toMinutes(neededTime) % 60),
                TimeUnit.MILLISECONDS.toSeconds(neededTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(neededTime)));
    }

    /**
     * Computes the time in milliseconds which passed between when the timer was
     * started and the present time.
     */
    public void stopMS() {
        endTime = System.nanoTime();
        neededTime = (endTime - startTime) / 1000000;
        log.info("{} ms", neededTime);
    }

    /**
     * Computes the time in milliseconds which passed between when the timer was
     * started and the present time.
     *
     * @param description - the description of the measured task
     */
    public void stopMS(String description) {
        endTime = System.nanoTime();
        neededTime = (endTime - startTime) / 1000000;
        log.info("{} {} ms", description, neededTime);
    }

    /**
     * Computes the time in seconds which passed between when the timer was
     * started and the present time.
     */
    public void stopSec() {
        endTime = System.nanoTime();
        neededTime = (endTime - startTime) / 1000000;
        log.info("{} sec", TimeUnit.MILLISECONDS.toSeconds(neededTime));
    }

    /**
     * Computes the time in seconds which passed between when the timer was
     * started and the present time.
     *
     * @param description - the description of the measured task
     */
    public void stopSec(String description) {
        endTime = System.nanoTime();
        neededTime = (endTime - startTime) / 1000000;
        log.info("{} {} sec", description, TimeUnit.MILLISECONDS.toSeconds(neededTime));
    }

    /**
     * Returns the difference between the start time and the stop time, in
     * milliseconds.
     *
     * @return - the amount of milliseconds passed between start and stop timer
     */
    public long getNeededTime() {
        return neededTime;
    }

    /**
     * Returns the time when the timer was started.
     *
     * @return - the time, in nanoseconds, when the timer was started
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Sets the time when the timer was started, for the case when the timer is
     * started by an external source.
     *
     * @param startTime - the time, in nanoseconds, when the timer was started
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Returns the difference between the start time and the current time
     *
     * @return - the amount of milliseconds passed between start and current time
     */
    public long readTimer() {
        if (startTime == 0) {
            return 0;
        } else {
            return (System.nanoTime() - startTime) / 1000000;
        }
    }

    /**
     * Returns the difference between the start time and the current time
     *
     * @param description timer description
     * @return - the amount of milliseconds passed between start and current time
     */
    public long readTimer(String description) {
        long retVal;
        if (startTime == 0) {
            retVal = 0;
        } else {
            retVal = (System.nanoTime() - startTime) / 1000000;
        }
        log.trace("{} time needed in miliseconds {}", description, retVal);
        return retVal;
    }
}
