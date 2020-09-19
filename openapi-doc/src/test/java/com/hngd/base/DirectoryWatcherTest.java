package com.hngd.base;

import java.io.IOException;

import com.hngd.utils.DirectoryWatcher;

public class DirectoryWatcherTest {

	 public static void main(String[] args) throws IOException
	    {
	        String fp="D:\\test";
	        @SuppressWarnings("unused")
	        DirectoryWatcher dw=new DirectoryWatcher(fp,()->{});
	    }
}
