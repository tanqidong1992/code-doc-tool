package com.hngd.parser.source;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import com.github.javaparser.ast.CompilationUnit;
import com.hngd.exception.SourceParseException;
import com.hngd.utils.ClassUtils;
import com.hngd.utils.JavaFileUtils;

public class CachedSourceParserContext extends SourceParserContext {

    private DB db ;
    public CachedSourceParserContext(String cacheDirectory) {
        this(null,null,cacheDirectory);
    }
    
    public CachedSourceParserContext(String includes,String excludes,String cacheDirectory) {
        super(includes, excludes);
        initLevelDB(cacheDirectory);
    }
    
    private void initLevelDB(String cachePath) {
        File cacheDirectory=new File(cachePath);
        File cacheFile=null;
        if(cacheDirectory.exists() || cacheDirectory.mkdirs()) {
            cacheFile=new File(cacheDirectory,"comment-store");
        }else {
            throw new RuntimeException("Cannot mkdir: "+cachePath);
        }
        Options options = new Options();
        options.compressionType(CompressionType.NONE);
        try {
            db = Iq80DBFactory.factory.open(cacheFile, options);
        } catch (IOException e) {
            throw new RuntimeException("Cannot initialize leveldb at "+cacheFile.getAbsolutePath(),e);
        }
    }

    @Override
    public FileParseResult doParseSourceFile(File f) {
        byte[] fileContent=null;
        try {
            fileContent = FileUtils.readFileToByteArray(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] sha2=JavaFileUtils.sha2(fileContent);
        Optional<FileParseResult> result=readCachedResult(sha2);
        if(result.isPresent()) {
            return result.get();
        }else {
            return parseAndCache(sha2,fileContent);
        }
    }
    
    @Override
    public FileParseResult doParseJarSourceEntry(JarFile jarFile, JarEntry je) {
        
        try(InputStream in=jarFile.getInputStream(je)){
            byte[] buffer=new byte[1024];
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            int temp=-1;
            while((temp=IOUtils.read(in,buffer,0, 1024))>0){
                baos.write(buffer, 0, temp);
            }
            byte[] fileContent=baos.toByteArray();
            byte[] sha2=JavaFileUtils.sha2(fileContent);
            Optional<FileParseResult> result=readCachedResult(sha2);
            if(result.isPresent()) {
                System.out.println("Parsing "+jarFile.getName()+"!"+je.getName()+" from cache");
                return result.get();
            }else {
                System.out.println("Parsing "+jarFile.getName()+"!"+je.getName()+" from file");
                return parseAndCache(sha2,fileContent);
            }
        }catch(Exception e) {
            String msg="Parse file:"+jarFile.getName()+"!"+je.getName()+" failed!";
            throw new SourceParseException(msg, e);
        }
        
    }

    private FileParseResult parseAndCache(byte[] sha2, byte[] fileContent) {
        CompilationUnit cu=ClassUtils.parseClass(fileContent);
        FileParseResult result=super.doParseCompilationUnit(cu);
        if(result!=null) {
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            try {
                new ObjectOutputStream(baos).writeObject(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            db.put(sha2, baos.toByteArray());
        }
        return result;
    }

    private Optional<FileParseResult> readCachedResult(byte[] sha2) {
        byte[] data=db.get(sha2);
        if(data==null) {
            return Optional.empty();
        }
        ByteArrayInputStream in=new ByteArrayInputStream(data);
        FileParseResult result=null;
        try {
            result = (FileParseResult) new ObjectInputStream(in).readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(result);
    }
}
