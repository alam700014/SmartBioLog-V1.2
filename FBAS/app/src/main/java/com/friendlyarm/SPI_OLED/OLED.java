package com.friendlyarm.SPI_OLED;

import android.util.Log;

import com.friendlyarm.AndroidSDK.FileCtlEnum;
import com.friendlyarm.AndroidSDK.GPIOEnum;
import com.friendlyarm.AndroidSDK.HardwareControler;

public class OLED {
	
	private int devFD = -1;
	private int gpioPin_For_DC = -1;
	private int gpioPin_For_Reset = -1;
	private boolean isValid = false;
	
	static final byte spiMode = 0;
	static final char spiBits = 8;
	static final int spiSpeed = 25000000;
	static short spiDelay;


	final String TAG = "OLED";
	
    final int OLED_WIDTH = 132;
    final int OLED_HEIGHT = 64;
	
	// 8 x 16 font
	static final short OLEDCharMap[]=
	{
	        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,//  0
	        0x00,0x00,0x00,0xF8,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x33,0x30,0x00,0x00,0x00,//! 1
	        0x00,0x10,0x0C,0x06,0x10,0x0C,0x06,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,//" 2
	        0x40,0xC0,0x78,0x40,0xC0,0x78,0x40,0x00,0x04,0x3F,0x04,0x04,0x3F,0x04,0x04,0x00,//# 3
	        0x00,0x70,0x88,0xFC,0x08,0x30,0x00,0x00,0x00,0x18,0x20,0xFF,0x21,0x1E,0x00,0x00,//$ 4
	        0xF0,0x08,0xF0,0x00,0xE0,0x18,0x00,0x00,0x00,0x21,0x1C,0x03,0x1E,0x21,0x1E,0x00,//% 5
	        0x00,0xF0,0x08,0x88,0x70,0x00,0x00,0x00,0x1E,0x21,0x23,0x24,0x19,0x27,0x21,0x10,//& 6
	        0x10,0x16,0x0E,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,//' 7
	        0x00,0x00,0x00,0xE0,0x18,0x04,0x02,0x00,0x00,0x00,0x00,0x07,0x18,0x20,0x40,0x00,//( 8
	        0x00,0x02,0x04,0x18,0xE0,0x00,0x00,0x00,0x00,0x40,0x20,0x18,0x07,0x00,0x00,0x00,//) 9
	        0x40,0x40,0x80,0xF0,0x80,0x40,0x40,0x00,0x02,0x02,0x01,0x0F,0x01,0x02,0x02,0x00,//* 10
	        0x00,0x00,0x00,0xF0,0x00,0x00,0x00,0x00,0x01,0x01,0x01,0x1F,0x01,0x01,0x01,0x00,//+ 11
	        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x80,0xB0,0x70,0x00,0x00,0x00,0x00,0x00,//, 12
	        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x01,0x01,0x01,0x01,0x01,0x01,0x01,//- 13
	        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x30,0x30,0x00,0x00,0x00,0x00,0x00,//. 14
	        0x00,0x00,0x00,0x00,0x80,0x60,0x18,0x04,0x00,0x60,0x18,0x06,0x01,0x00,0x00,0x00,/// 15
	        0x00,0xE0,0x10,0x08,0x08,0x10,0xE0,0x00,0x00,0x0F,0x10,0x20,0x20,0x10,0x0F,0x00,//0 16
	        0x00,0x10,0x10,0xF8,0x00,0x00,0x00,0x00,0x00,0x20,0x20,0x3F,0x20,0x20,0x00,0x00,//1 17
	        0x00,0x70,0x08,0x08,0x08,0x88,0x70,0x00,0x00,0x30,0x28,0x24,0x22,0x21,0x30,0x00,//2 18
	        0x00,0x30,0x08,0x88,0x88,0x48,0x30,0x00,0x00,0x18,0x20,0x20,0x20,0x11,0x0E,0x00,//3 19
	        0x00,0x00,0xC0,0x20,0x10,0xF8,0x00,0x00,0x00,0x07,0x04,0x24,0x24,0x3F,0x24,0x00,//4 20
	        0x00,0xF8,0x08,0x88,0x88,0x08,0x08,0x00,0x00,0x19,0x21,0x20,0x20,0x11,0x0E,0x00,//5 21
	        0x00,0xE0,0x10,0x88,0x88,0x18,0x00,0x00,0x00,0x0F,0x11,0x20,0x20,0x11,0x0E,0x00,//6 22
	        0x00,0x38,0x08,0x08,0xC8,0x38,0x08,0x00,0x00,0x00,0x00,0x3F,0x00,0x00,0x00,0x00,//7 23
	        0x00,0x70,0x88,0x08,0x08,0x88,0x70,0x00,0x00,0x1C,0x22,0x21,0x21,0x22,0x1C,0x00,//8 24
	        0x00,0xE0,0x10,0x08,0x08,0x10,0xE0,0x00,0x00,0x00,0x31,0x22,0x22,0x11,0x0F,0x00,//9 25
	        0x00,0x00,0x00,0xC0,0xC0,0x00,0x00,0x00,0x00,0x00,0x00,0x30,0x30,0x00,0x00,0x00,//: 26
	        0x00,0x00,0x00,0x80,0x00,0x00,0x00,0x00,0x00,0x00,0x80,0x60,0x00,0x00,0x00,0x00,//; 27
	        0x00,0x00,0x80,0x40,0x20,0x10,0x08,0x00,0x00,0x01,0x02,0x04,0x08,0x10,0x20,0x00,//< 28
	        0x40,0x40,0x40,0x40,0x40,0x40,0x40,0x00,0x04,0x04,0x04,0x04,0x04,0x04,0x04,0x00,//= 29
	        0x00,0x08,0x10,0x20,0x40,0x80,0x00,0x00,0x00,0x20,0x10,0x08,0x04,0x02,0x01,0x00,//> 30
	        0x00,0x70,0x48,0x08,0x08,0x08,0xF0,0x00,0x00,0x00,0x00,0x30,0x36,0x01,0x00,0x00,//? 31
	        0xC0,0x30,0xC8,0x28,0xE8,0x10,0xE0,0x00,0x07,0x18,0x27,0x24,0x23,0x14,0x0B,0x00,//@ 32
	        0x00,0x00,0xC0,0x38,0xE0,0x00,0x00,0x00,0x20,0x3C,0x23,0x02,0x02,0x27,0x38,0x20,//A 33
	        0x08,0xF8,0x88,0x88,0x88,0x70,0x00,0x00,0x20,0x3F,0x20,0x20,0x20,0x11,0x0E,0x00,//B 34
	        0xC0,0x30,0x08,0x08,0x08,0x08,0x38,0x00,0x07,0x18,0x20,0x20,0x20,0x10,0x08,0x00,//C 35
	        0x08,0xF8,0x08,0x08,0x08,0x10,0xE0,0x00,0x20,0x3F,0x20,0x20,0x20,0x10,0x0F,0x00,//D 36
	        0x08,0xF8,0x88,0x88,0xE8,0x08,0x10,0x00,0x20,0x3F,0x20,0x20,0x23,0x20,0x18,0x00,//E 37
	        0x08,0xF8,0x88,0x88,0xE8,0x08,0x10,0x00,0x20,0x3F,0x20,0x00,0x03,0x00,0x00,0x00,//F 38
	        0xC0,0x30,0x08,0x08,0x08,0x38,0x00,0x00,0x07,0x18,0x20,0x20,0x22,0x1E,0x02,0x00,//G 39
	        0x08,0xF8,0x08,0x00,0x00,0x08,0xF8,0x08,0x20,0x3F,0x21,0x01,0x01,0x21,0x3F,0x20,//H 40
	        0x00,0x08,0x08,0xF8,0x08,0x08,0x00,0x00,0x00,0x20,0x20,0x3F,0x20,0x20,0x00,0x00,//I 41
	        0x00,0x00,0x08,0x08,0xF8,0x08,0x08,0x00,0xC0,0x80,0x80,0x80,0x7F,0x00,0x00,0x00,//J 42
	        0x08,0xF8,0x88,0xC0,0x28,0x18,0x08,0x00,0x20,0x3F,0x20,0x01,0x26,0x38,0x20,0x00,//K 43
	        0x08,0xF8,0x08,0x00,0x00,0x00,0x00,0x00,0x20,0x3F,0x20,0x20,0x20,0x20,0x30,0x00,//L 44
	        0x08,0xF8,0xF8,0x00,0xF8,0xF8,0x08,0x00,0x20,0x3F,0x00,0x3F,0x00,0x3F,0x20,0x00,//M 45
	        0x08,0xF8,0x30,0xC0,0x00,0x08,0xF8,0x08,0x20,0x3F,0x20,0x00,0x07,0x18,0x3F,0x00,//N 46
	        0xE0,0x10,0x08,0x08,0x08,0x10,0xE0,0x00,0x0F,0x10,0x20,0x20,0x20,0x10,0x0F,0x00,//O 47
	        0x08,0xF8,0x08,0x08,0x08,0x08,0xF0,0x00,0x20,0x3F,0x21,0x01,0x01,0x01,0x00,0x00,//P 48
	        0xE0,0x10,0x08,0x08,0x08,0x10,0xE0,0x00,0x0F,0x18,0x24,0x24,0x38,0x50,0x4F,0x00,//Q 49
	        0x08,0xF8,0x88,0x88,0x88,0x88,0x70,0x00,0x20,0x3F,0x20,0x00,0x03,0x0C,0x30,0x20,//R 50
	        0x00,0x70,0x88,0x08,0x08,0x08,0x38,0x00,0x00,0x38,0x20,0x21,0x21,0x22,0x1C,0x00,//S 51
	        0x18,0x08,0x08,0xF8,0x08,0x08,0x18,0x00,0x00,0x00,0x20,0x3F,0x20,0x00,0x00,0x00,//T 52
	        0x08,0xF8,0x08,0x00,0x00,0x08,0xF8,0x08,0x00,0x1F,0x20,0x20,0x20,0x20,0x1F,0x00,//U 53
	        0x08,0x78,0x88,0x00,0x00,0xC8,0x38,0x08,0x00,0x00,0x07,0x38,0x0E,0x01,0x00,0x00,//V 54
	        0xF8,0x08,0x00,0xF8,0x00,0x08,0xF8,0x00,0x03,0x3C,0x07,0x00,0x07,0x3C,0x03,0x00,//W 55
	        0x08,0x18,0x68,0x80,0x80,0x68,0x18,0x08,0x20,0x30,0x2C,0x03,0x03,0x2C,0x30,0x20,//X 56
	        0x08,0x38,0xC8,0x00,0xC8,0x38,0x08,0x00,0x00,0x00,0x20,0x3F,0x20,0x00,0x00,0x00,//Y 57
	        0x10,0x08,0x08,0x08,0xC8,0x38,0x08,0x00,0x20,0x38,0x26,0x21,0x20,0x20,0x18,0x00,//Z 58
	        0x00,0x00,0x00,0xFE,0x02,0x02,0x02,0x00,0x00,0x00,0x00,0x7F,0x40,0x40,0x40,0x00,//[ 59
	        0x00,0x0C,0x30,0xC0,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x01,0x06,0x38,0xC0,0x00,//\ 60
	        0x00,0x02,0x02,0x02,0xFE,0x00,0x00,0x00,0x00,0x40,0x40,0x40,0x7F,0x00,0x00,0x00,//] 61
	        0x00,0x00,0x04,0x02,0x02,0x02,0x04,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,//^ 62
	        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x80,0x80,0x80,0x80,0x80,0x80,0x80,0x80,//_ 63
	        0x00,0x02,0x02,0x04,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,//` 64
	        0x00,0x00,0x80,0x80,0x80,0x80,0x00,0x00,0x00,0x19,0x24,0x22,0x22,0x22,0x3F,0x20,//a 65
	        0x08,0xF8,0x00,0x80,0x80,0x00,0x00,0x00,0x00,0x3F,0x11,0x20,0x20,0x11,0x0E,0x00,//b 66
	        0x00,0x00,0x00,0x80,0x80,0x80,0x00,0x00,0x00,0x0E,0x11,0x20,0x20,0x20,0x11,0x00,//c 67
	        0x00,0x00,0x00,0x80,0x80,0x88,0xF8,0x00,0x00,0x0E,0x11,0x20,0x20,0x10,0x3F,0x20,//d 68
	        0x00,0x00,0x80,0x80,0x80,0x80,0x00,0x00,0x00,0x1F,0x22,0x22,0x22,0x22,0x13,0x00,//e 69
	        0x00,0x80,0x80,0xF0,0x88,0x88,0x88,0x18,0x00,0x20,0x20,0x3F,0x20,0x20,0x00,0x00,//f 70
	        0x00,0x00,0x80,0x80,0x80,0x80,0x80,0x00,0x00,0x6B,0x94,0x94,0x94,0x93,0x60,0x00,//g 71
	        0x08,0xF8,0x00,0x80,0x80,0x80,0x00,0x00,0x20,0x3F,0x21,0x00,0x00,0x20,0x3F,0x20,//h 72
	        0x00,0x80,0x98,0x98,0x00,0x00,0x00,0x00,0x00,0x20,0x20,0x3F,0x20,0x20,0x00,0x00,//i 73
	        0x00,0x00,0x00,0x80,0x98,0x98,0x00,0x00,0x00,0xC0,0x80,0x80,0x80,0x7F,0x00,0x00,//j 74
	        0x08,0xF8,0x00,0x00,0x80,0x80,0x80,0x00,0x20,0x3F,0x24,0x02,0x2D,0x30,0x20,0x00,//k 75
	        0x00,0x08,0x08,0xF8,0x00,0x00,0x00,0x00,0x00,0x20,0x20,0x3F,0x20,0x20,0x00,0x00,//l 76
	        0x80,0x80,0x80,0x80,0x80,0x80,0x80,0x00,0x20,0x3F,0x20,0x00,0x3F,0x20,0x00,0x3F,//m 77
	        0x80,0x80,0x00,0x80,0x80,0x80,0x00,0x00,0x20,0x3F,0x21,0x00,0x00,0x20,0x3F,0x20,//n 78
	        0x00,0x00,0x80,0x80,0x80,0x80,0x00,0x00,0x00,0x1F,0x20,0x20,0x20,0x20,0x1F,0x00,//o 79
	        0x80,0x80,0x00,0x80,0x80,0x00,0x00,0x00,0x80,0xFF,0xA1,0x20,0x20,0x11,0x0E,0x00,//p 80
	        0x00,0x00,0x00,0x80,0x80,0x80,0x80,0x00,0x00,0x0E,0x11,0x20,0x20,0xA0,0xFF,0x80,//q 81
	        0x80,0x80,0x80,0x00,0x80,0x80,0x80,0x00,0x20,0x20,0x3F,0x21,0x20,0x00,0x01,0x00,//r 82
	        0x00,0x00,0x80,0x80,0x80,0x80,0x80,0x00,0x00,0x33,0x24,0x24,0x24,0x24,0x19,0x00,//s 83
	        0x00,0x80,0x80,0xE0,0x80,0x80,0x00,0x00,0x00,0x00,0x00,0x1F,0x20,0x20,0x00,0x00,//t 84
	        0x80,0x80,0x00,0x00,0x00,0x80,0x80,0x00,0x00,0x1F,0x20,0x20,0x20,0x10,0x3F,0x20,//u 85
	        0x80,0x80,0x80,0x00,0x00,0x80,0x80,0x80,0x00,0x01,0x0E,0x30,0x08,0x06,0x01,0x00,//v 86
	        0x80,0x80,0x00,0x80,0x00,0x80,0x80,0x80,0x0F,0x30,0x0C,0x03,0x0C,0x30,0x0F,0x00,//w 87
	        0x00,0x80,0x80,0x00,0x80,0x80,0x80,0x00,0x00,0x20,0x31,0x2E,0x0E,0x31,0x20,0x00,//x 88
	        0x80,0x80,0x80,0x00,0x00,0x80,0x80,0x80,0x80,0x81,0x8E,0x70,0x18,0x06,0x01,0x00,//y 89
	        0x00,0x80,0x80,0x80,0x80,0x80,0x80,0x00,0x00,0x21,0x30,0x2C,0x22,0x21,0x30,0x00,//z 90
	        0x00,0x00,0x00,0x00,0x80,0x7C,0x02,0x02,0x00,0x00,0x00,0x00,0x00,0x3F,0x40,0x40,//{ 91
	        0x00,0x00,0x00,0x00,0xFF,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0xFF,0x00,0x00,0x00,//| 92
	        0x00,0x02,0x02,0x7C,0x80,0x00,0x00,0x00,0x00,0x40,0x40,0x3F,0x00,0x00,0x00,0x00,//} 93
	        0x00,0x06,0x01,0x01,0x02,0x02,0x04,0x04,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,//~ 94
	};

	// write 8 * 8 bitmap
	int OLEDWriteData(byte data)
	{       
	    int ret = 0;
	    final int pin = gpioPin_For_DC;

	    if (HardwareControler.setGPIODirection(pin, GPIOEnum.OUT) == -1) {
	        ret = -1;
	    }
	    if (HardwareControler.setGPIOValue(pin, GPIOEnum.HIGH) == -1) {
	        ret = -1;
	    }       
	    if (HardwareControler.SPItransferOneByte(devFD, data, spiDelay, spiSpeed, spiBits) == -1) {
	        ret = -1;        
	    }
	    return ret;
	}

	int OLEDWriteCmd(byte cmd)
	{
	    int ret = 0;
	    final int pin = gpioPin_For_DC;

	    if (HardwareControler.setGPIODirection(pin, GPIOEnum.OUT) == -1) {
	        ret = -1;
	    }
	    if (HardwareControler.setGPIOValue(pin, GPIOEnum.LOW) == -1) {
	        ret = -1;
	    }
	    
	    if (HardwareControler.SPItransferOneByte(devFD, cmd, spiDelay, spiSpeed, spiBits) == -1) {
	        ret = -1;        
	    }
	    return ret;
	}

	int OLEDSetPos(int x, int y)
	{ 
	    int ret = 0;

	    y = y/8 + y%8;
	    if (OLEDWriteCmd((byte)(0xb0+y)) == -1) {
	        ret = -1;
	    }
	    
	    if (OLEDWriteCmd((byte)((x&0xf0)>>4|0x10)) == -1) {
	        ret = -1;
	    }
	    if (OLEDWriteCmd((byte)((x&0x0f)|0x01)) == -1) {
	        ret = -1;
	    }
	    return ret;
	} 

	int OLEDReset(int pin) throws InterruptedException
	{
	    int ret = -1;

	    if ((ret = HardwareControler.setGPIODirection(pin, GPIOEnum.OUT)) == -1) {
	        return -1;
	    }
	    if ((ret = HardwareControler.setGPIOValue(pin, GPIOEnum.LOW)) == -1) {
	        return -1;
	    }    
	    Thread.sleep(0, 100);
	    if ((ret = HardwareControler.setGPIOValue(pin, GPIOEnum.HIGH)) == -1) {
	        return -1;
	    }
	    return ret;
	}
	
	int OLEDSPIInit()
	{
	    if (HardwareControler.setSPIDataMode(devFD, spiMode) != 0) {
	        return -1;
	    }
	    
	    if (HardwareControler.setSPIWriteBitsPerWord(devFD, spiBits) != 0) {
	        return -1;
	    }
	    if (HardwareControler.setSPIMaxSpeed(devFD, spiSpeed) == -1 ) {
	        return -1;
	    }
	    return 0;
	}
	
	void Deinit() {
		if (devFD >= 0) {
	    	HardwareControler.close(devFD);
	    	devFD = -1;
		}
	}

	int Init(String spiDevPath, int cmdDatPin, int resetPin) 
	{
		gpioPin_For_Reset = resetPin;
		gpioPin_For_DC = cmdDatPin;

		devFD = HardwareControler.open( spiDevPath, FileCtlEnum.O_RDWR );
		if (devFD < 0) {
			Log.e(TAG, "Fail to open SPI device: " + spiDevPath);
			return -1;
		}
		Log.d(TAG, "Open " + spiDevPath + " Ok.");
		if (OLEDSPIInit() == -1) {
			Deinit();
			Log.e(TAG, "Fail to init SPI device OLED");
			return -1;
		}
		try {
			if (OLEDReset(gpioPin_For_Reset) == -1) {
				Deinit();
				Log.e(TAG, "Fail to reset OLED");
				return -1;
			}
		} catch  (Exception e) {
			Log.e(TAG, "Fail to reset OLED");
			return -1;
		}
		if (OLEDWriteCmd((byte)0xae) == -1) {//--turn off oled panel
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}

		if (OLEDWriteCmd((byte)0xae) == -1) {//--turn off oled panel
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0x00) == -1) {//---set low column address
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0x10) == -1) {//---set high column address
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0x40) == -1) {//--set start line address  Set Mapping RAM Display       Start Line (0x00~0x3F)
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}

		if (OLEDWriteCmd((byte)0x81) == -1) {//--set contrast control register
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}
		
		if (OLEDWriteCmd((byte)0xcf) == -1) { // Set SEG Output Current Brightness
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0xa1) == -1) {//--Set SEG/Column Mapping     0xa0左右反置 0xa1正常
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0xc8) == -1) {//Set COM/Row Scan Direction   0xc0上下反置 0xc8正常
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0xa6) == -1) {//--set normal display
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0xa8) == -1) {//--set multiplex ratio(1 to 64)
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0x3f) == -1) {//--1/64 duty
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0xd3) == -1) {//-set display offset Shift Mapping RAM Counter (0x00~0x3F)
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0x00) == -1) {//-not offset
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0xd5) == -1) {//--set display clock divide ratio/oscillator frequency
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0x80) == -1) {//--set divide ratio, Set Clock as 100 Frames/Sec
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0xd9) == -1) {//--set pre-charge period
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0xf1) == -1) {//Set Pre-Charge as 15 Clocks & Discharge as 1 Clock
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0xda) == -1) {//--set com pins hardware configuration
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0x12) == -1) {
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0xdb) == -1) {//--set vcomh
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0x40) == -1) {//Set VCOM Deselect Level
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0x20) == -1) {//-Set Page Addressing Mode (0x00/0x01/0x02)
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0x02) == -1) {//
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0x8d) == -1) {//--set Charge Pump enable/disable
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0x14) == -1){//--set(0x10) disable
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}

		if (OLEDWriteCmd((byte)0xa4) == -1) {// Disable Entire Display On (0xa4/0xa5)
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0xa6) == -1) {// Disable Inverse Display On (0xa6/a7) 
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDWriteCmd((byte)0xaf) == -1) {//--turn on oled panel
			Deinit();
			Log.e(TAG, "OLED write cmd failed");
			return -1;
		}


		if (OLEDSetPos(0, 0) == -1) {
			Deinit();
			Log.e(TAG, "OLEDSetPos failed");
			return -1;
		}

		return 0;
	}

	int OLEDDisp8x16Char(int x, int y, byte ch)
	{
	    int i = 0;
	    final int index = ch-32; 

	    if (x<0 || x>(OLED_WIDTH-8) || y<0 || y>(OLED_HEIGHT-16)) {
	    	Log.e(TAG, "Unsupported OLED coordinate");
	        return -1;
	    }

	    OLEDSetPos(x, y);    
	    for (i=0; i<8; i++) {  
	        OLEDWriteData((byte)OLEDCharMap[index*16+i]);
	    }

	    OLEDSetPos(x, y+8);    
	    for (i=0; i<8; i++)     
	        OLEDWriteData((byte)OLEDCharMap[index*16+i+8]); 

	    return 0;
	}

	int OLEDDisp8x16Str(int x, int y, byte[] strBytes)
	{
		int xx = x;
        for (int i = 0; i < strBytes.length; i++) {
	        if (OLEDDisp8x16Char(xx, y, strBytes[i]) == -1) {
	        	Log.e(TAG, "Fail to write 8x16 Char to OLED");
	            return -1;
	        }
	        xx=xx+8;
		}
	    return 0;
	}

	int OLEDCleanScreen()
	{
	    int x,y;
	    final byte data = 0x00;
	    int ret = 0;
	    for (y=0; y<8; y++) {
	        OLEDWriteCmd((byte)(0xb0+y));
	        OLEDWriteCmd((byte)(0x00));
	        OLEDWriteCmd((byte)(0x10));
	        for (x=0; x<OLED_WIDTH; x++) {
	            if (OLEDWriteData(data) == -1) {
	                ret = -1;
	                break;
	            }
	        }
	    }
	    return ret;
	} 

}
