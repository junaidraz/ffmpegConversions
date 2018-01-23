package com.test.ffmpeg;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class ConvertVideo {

    public static String folderPath="/home/junaid/tmpdata/youdl";

    public static void readFilesDirectory() throws IOException, InterruptedException {
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();


        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                //System.out.println("File " + listOfFiles[i].getName());
                String fullFileName =  listOfFiles[i].getName();
                String[] split = fullFileName.split("\\.");
                //System.out.println(split[0]+" : "+split[1]);
                if(!split[1].equalsIgnoreCase("mpg")) {
                    String inputFile = folderPath + "/" + listOfFiles[i].getName();
                    String outputFile = folderPath + "/" + split[0] + ".mpg";
                    System.out.println("\n *** Generating File " + outputFile + " *** ");
                    converVideo(inputFile, outputFile);
                }

            } else if (listOfFiles[i].isDirectory()) {
                //System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
    }

    public static void converVideo(String inputFile ,String outputFile) throws IOException {

        FFmpeg ffmpeg = new FFmpeg("ffmpeg");
        FFprobe ffprobe = new FFprobe("ffprobe");

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        final FFmpegProbeResult in = ffprobe.probe(inputFile);

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(in) // Or filename
                .addOutput(outputFile)
                .done();

        FFmpegJob job = executor.createJob(builder, new ProgressListener() {

            // Using the FFmpegProbeResult determine the duration of the input
            final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

            @Override
            public void progress(Progress progress) {
                double percentage = progress.out_time_ns / duration_ns;

                //long prog = (new Double(percentage*100)).longValue();
                //System.out.println(percentage +" : "+prog);
                //printProgress(0l,100l,prog);

                // Print out interesting information about the progress
                System.out.print("\r"+String.format(
                        "[%.0f%%] status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx",
                        percentage * 100,
                        progress.status,
                        progress.frame,
                        FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
                        progress.fps.doubleValue(),
                        progress.speed
                ));
            }
        });

        job.run();
    }

    private static void printProgress(long startTime, long total, long current) {
        try {
            long eta = current == 0 ? 0 :
                    (total - current) * (System.currentTimeMillis() - startTime) / current;

            String etaHms = current == 0 ? "N/A" :
                    String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                            TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

            StringBuilder string = new StringBuilder(140);
            int percent = (int) (current * 100 / total);
            string
                    .append('\r')
                    .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
                    .append(String.format(" %d%% [", percent))
                    .append(String.join("", Collections.nCopies(percent, "=")))
                    .append('>')
                    .append(String.join("", Collections.nCopies(100 - percent, " ")))
                    .append(']')
                    .append(String.join("", Collections.nCopies((int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
                    .append(String.format(" %d/%d, ETA: %s", current, total, etaHms));

            System.out.print(string);
        }catch (IllegalArgumentException e){
            System.out.println(" ");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void showpercent() throws InterruptedException {
        int progress = 10;
        for(progress = 10; progress<=100; progress+=10) {
            System.out.print("\rPercent = " + (progress) + "%");
            Thread.sleep(3000);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        /*String log4jConfPath = "log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);*/

        readFilesDirectory();
    }
}
