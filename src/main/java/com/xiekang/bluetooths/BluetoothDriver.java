/* Copyright 2011-2013 Google Inc.
 * Copyright 2013 mike wakerly <opensource@hoho.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * Project home page: https://github.com/mik3y/usb-serial-for-android
 */

package com.xiekang.bluetooths;


import android.bluetooth.BluetoothDevice;

import com.xiekang.bluetooths.interfaces.Bluetooth_Satus;

/**
 *
 * @author hu
 */
public interface BluetoothDriver<T extends Bluetooth_Satus> {

    /**
     * 连接
     * @param bluetoothDevice
     * @param date 数据接口
     */
    void Connect(BluetoothDevice bluetoothDevice, T date,Bluetooth_Satus bluetooth_satus);
    //断开连接
    void UnRegisterReceiver();

}
