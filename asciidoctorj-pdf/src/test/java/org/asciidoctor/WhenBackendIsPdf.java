package org.asciidoctor;

import org.asciidoctor.util.RougeColors;
import org.asciidoctor.util.pdf.ColorsProcessor;
import org.asciidoctor.util.pdf.ImageProcessor;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.asciidoctor.OptionsBuilder.options;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class WhenBackendIsPdf {

    private Asciidoctor asciidoctor;


    @Before
    public void initAsciidoctor() {
        this.asciidoctor = Asciidoctor.Factory.create();
    }

    @Test
    public void pdf_should_include_images() throws IOException {
        String filename = "image-sample";
        File inputFile = new File("build/resources/test/" + filename + ".adoc");
        File outputFile1 = new File(inputFile.getParentFile(), filename + ".pdf");
        removeFileIfItExists(outputFile1);

        asciidoctor.convertFile(inputFile, options().backend("pdf").safe(SafeMode.UNSAFE).get());

        assertThat(outputFile1.exists(), is(true));
        ImageProcessor imageProcessor = new ImageProcessor();
        imageProcessor.parse(outputFile1.getAbsolutePath());
        List images = imageProcessor.getImages();
        assertThat(images.size(), is(2));
    }

    @Test
    public void pdf_source_code_should_be_highlighted() throws IOException {
        String filename = "code-sample";
        File inputFile = new File("build/resources/test/" + filename + ".adoc");
        File outputFile1 = new File(inputFile.getParentFile(), filename + ".pdf");
        removeFileIfItExists(outputFile1);

        asciidoctor.convertFile(inputFile, options().backend("pdf").safe(SafeMode.UNSAFE).get());

        assertThat(outputFile1.exists(), is(true));

        ColorsProcessor colorsProcessor = new ColorsProcessor("program", "System.out.println", "printHello", "HelloWorld", "<body>", "else", "Math.sqrt");
        colorsProcessor.parse(outputFile1.getAbsolutePath());
        Map<String, List<Color>> colors = colorsProcessor.getColors();
        assertThat(colors.get("program").get(0), equalTo(RougeColors.GREY));
        assertThat(colors.get("System.out.println").get(0), equalTo(RougeColors.LIGHT_BLUE));
        assertThat(colors.get("printHello").get(0), equalTo(RougeColors.DARK_BLUE));
        assertThat(colors.get("HelloWorld").get(0), equalTo(RougeColors.PINK));
        assertThat(colors.get("<body>").get(0), equalTo(RougeColors.PINK));
        assertThat(colors.get("else").get(0), equalTo(RougeColors.GREEN));
        assertThat(colors.get("Math.sqrt").get(0), equalTo(RougeColors.LIGHT_BLUE));
    }

    @Test
    public void pdf_text_should_be_hyphenated_german() throws IOException {
        String filename = "hyphenation-de-sample";
        File inputFile = new File("build/resources/test/" + filename + ".adoc");
        File outputFile1 = new File(inputFile.getParentFile(), filename + ".pdf");
        removeFileIfItExists(outputFile1);

        asciidoctor.convertFile(inputFile, options().backend("pdf").safe(SafeMode.UNSAFE).get());

        assertThat(outputFile1.exists(), is(true));

        ColorsProcessor colorsProcessor = new ColorsProcessor("Feh\u00adler");
        colorsProcessor.parse(outputFile1.getAbsolutePath());
        Map<String, List<Color>> words = colorsProcessor.getColors();
        assertThat(words.keySet(), hasItem("Feh\u00adler"));
    }

    @Test
    public void pdf_text_should_be_hyphenated_english() throws IOException {
        String filename = "hyphenation-en-sample";
        File inputFile = new File("build/resources/test/" + filename + ".adoc");
        File outputFile1 = new File(inputFile.getParentFile(), filename + ".pdf");
        removeFileIfItExists(outputFile1);

        asciidoctor.convertFile(inputFile, options().backend("pdf").safe(SafeMode.UNSAFE).get());

        assertThat(outputFile1.exists(), is(true));

        ColorsProcessor colorsProcessor = new ColorsProcessor("van\u00adquish");
        colorsProcessor.parse(outputFile1.getAbsolutePath());
        Map<String, List<Color>> words = colorsProcessor.getColors();
        assertThat(words.keySet(), hasItem("van\u00adquish"));
    }

    private void removeFileIfItExists(File file) throws IOException {
        if (file.exists()) {
            if (!file.delete()) {
                throw new IOException("can't delete file");
            }
        }
    }

}
