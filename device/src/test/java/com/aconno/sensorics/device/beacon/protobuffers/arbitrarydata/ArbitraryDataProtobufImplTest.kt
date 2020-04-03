package com.aconno.sensorics.device.beacon.protobuffers.arbitrarydata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aconno.sensorics.device.beacon.protobuffers.generatedmodel.ArbitraryDataProtobufModel
import com.aconno.sensorics.device.bluetooth.tasks.CharacteristicReadTask
import com.aconno.sensorics.device.bluetooth.tasks.CharacteristicWriteTask
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.UINT32
import com.aconno.sensorics.domain.migrate.getValueForUpdate
import com.aconno.sensorics.domain.scanning.BluetoothTaskProcessor
import com.google.protobuf.InvalidProtocolBufferException
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.lang.IllegalStateException
import java.util.zip.CRC32


class ArbitraryDataProtobufImplTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()


    @Test
    fun testSimpleOneFragmentReading() {
        val arbitraryDataImpl = ArbitraryDataProtobufImpl()
        val readTask = arbitraryDataImpl.read(getBluetoothTaskProcessorMock()) as CharacteristicReadTask

        readTask.onSuccess(buildTestReading(getSimpleProtobufModel()))

        assertThat(arbitraryDataImpl.capacity, `is`(TEST_CAPACITY))
    }

    @Test
    fun testFullModelOneFragmentReading() {
        val arbitraryDataImpl = ArbitraryDataProtobufImpl()
        val readTask = arbitraryDataImpl.read(getBluetoothTaskProcessorMock()) as CharacteristicReadTask

        readTask.onSuccess(buildTestReading(getFullProtobufModel()))

        verifyFullProtobufModel(arbitraryDataImpl)
    }

    @Test
    fun testFragmentedReading() {
        val arbitraryDataImpl = ArbitraryDataProtobufImpl()
        val readTask = arbitraryDataImpl.read(getBluetoothTaskProcessorMock()) as CharacteristicReadTask

        val bytes = buildTestReading(getFullProtobufModel())
        val fragments = fragmentByteArray(bytes,bytes.size/3)
        fragments.forEach {
            readTask.onSuccess(it)
        }

        verifyFullProtobufModel(arbitraryDataImpl)
    }

    @Test(expected = InvalidProtocolBufferException::class)
    fun testFragmentedCorruptedReading() {
        val arbitraryDataImpl = ArbitraryDataProtobufImpl()
        val readTask = arbitraryDataImpl.read(getBluetoothTaskProcessorMock()) as CharacteristicReadTask

        val bytes = buildTestReading(getFullProtobufModel())
        bytes[10] = (bytes[10] + 5).toByte() //changing some byte to simulate corrupted reading
        val fragments = fragmentByteArray(bytes,bytes.size/3)
        fragments.forEach {
            readTask.onSuccess(it)
        }

        verifyFullProtobufModel(arbitraryDataImpl)
    }

    @Test(expected = IllegalStateException::class)
    fun testFragmentedInvalidCrcReading() {
        val arbitraryDataImpl = ArbitraryDataProtobufImpl()
        val readTask = arbitraryDataImpl.read(getBluetoothTaskProcessorMock()) as CharacteristicReadTask

        val bytes = buildTestReading(getFullProtobufModel())
        bytes[bytes.lastIndex] = (bytes.last() + 5).toByte() //last byte is part of crc value so this simulates receiving reading with invalid crc value
        val fragments = fragmentByteArray(bytes,bytes.size/3)
        fragments.forEach {
            readTask.onSuccess(it)
        }

        verifyFullProtobufModel(arbitraryDataImpl)
    }


    @Test(expected = InvalidProtocolBufferException::class)
    fun testCorruptedReading() {
        val arbitraryDataImpl = ArbitraryDataProtobufImpl()
        val readTask = arbitraryDataImpl.read(getBluetoothTaskProcessorMock()) as CharacteristicReadTask

        val bytes = buildTestReading(getFullProtobufModel())
        bytes[10] = (bytes[10] + 5).toByte() //changing some byte to simulate corrupted reading
        readTask.onSuccess(bytes)
    }

    @Test(expected = IllegalStateException::class)
    fun testInvalidCrcReading() {
        val arbitraryDataImpl = ArbitraryDataProtobufImpl()
        val readTask = arbitraryDataImpl.read(getBluetoothTaskProcessorMock()) as CharacteristicReadTask

        val bytes = buildTestReading(getFullProtobufModel())
        bytes[bytes.lastIndex] = (bytes.last() + 5).toByte() //last byte is part of crc value so this simulates receiving reading with invalid crc value
        readTask.onSuccess(bytes)
    }

    @Test
    fun testBytesAvailableChangesOnEmptyModel() {
        val arbitraryDataImpl = ArbitraryDataProtobufImpl()
        val readTask = arbitraryDataImpl.read(getBluetoothTaskProcessorMock()) as CharacteristicReadTask

        val bytes = buildTestReading(getSimpleProtobufModel())
        readTask.onSuccess(bytes)

        assertThat(arbitraryDataImpl.available.value, `is`(TEST_CAPACITY))

        arbitraryDataImpl[SAMPLE_KEY_1] = SAMPLE_VALUE_1

        var serializedMapSize = ArbitraryDataProtobufModel.Values.newBuilder()
            .putValues(SAMPLE_KEY_1, SAMPLE_VALUE_1)
            .build().serializedSize

        assertThat(arbitraryDataImpl.available.value, `is`(TEST_CAPACITY - serializedMapSize))

        arbitraryDataImpl[SAMPLE_KEY_2] = SAMPLE_VALUE_2

        serializedMapSize = ArbitraryDataProtobufModel.Values.newBuilder()
            .putValues(SAMPLE_KEY_1, SAMPLE_VALUE_1)
            .putValues(SAMPLE_KEY_2, SAMPLE_VALUE_2)
            .build().serializedSize

        assertThat(arbitraryDataImpl.available.value, `is`(TEST_CAPACITY - serializedMapSize))
    }

    @Test
    fun testBytesAvailableChangesOnPrefilledModel() {
        val arbitraryDataImpl = ArbitraryDataProtobufImpl()
        val readTask = arbitraryDataImpl.read(getBluetoothTaskProcessorMock()) as CharacteristicReadTask

        val bytes = buildTestReading(getFullProtobufModel())
        readTask.onSuccess(bytes)

        val newKey = "newKey"
        val newValue = "newValue"
        arbitraryDataImpl[newKey] = newValue

        val serializedMapSize = ArbitraryDataProtobufModel.Values.newBuilder()
            .putValues(SAMPLE_KEY_1, SAMPLE_VALUE_1)
            .putValues(SAMPLE_KEY_2, SAMPLE_VALUE_2)
            .putValues(newKey,newValue)
            .build().serializedSize

        assertThat(arbitraryDataImpl.available.value, `is`(TEST_CAPACITY - serializedMapSize))
    }

    @Test
    fun testBytesAvailableChangesWhenRemovingEntries() {
        val arbitraryDataImpl = ArbitraryDataProtobufImpl()
        val readTask = arbitraryDataImpl.read(getBluetoothTaskProcessorMock()) as CharacteristicReadTask

        val bytes = buildTestReading(getFullProtobufModel())
        readTask.onSuccess(bytes)

        arbitraryDataImpl.removeEntry(SAMPLE_KEY_1)

        val serializedMapSize = ArbitraryDataProtobufModel.Values.newBuilder()
            .putValues(SAMPLE_KEY_2, SAMPLE_VALUE_2)
            .build().serializedSize

        assertThat(arbitraryDataImpl.available.value, `is`(TEST_CAPACITY - serializedMapSize))

        arbitraryDataImpl.removeEntry(SAMPLE_KEY_2)

        assertThat(arbitraryDataImpl.available.value, `is`(TEST_CAPACITY))
    }

    @Test
    fun testDirtinessWhenAddingNewEntry() {
        val arbitraryDataImpl = ArbitraryDataProtobufImpl()
        val readTask = arbitraryDataImpl.read(getBluetoothTaskProcessorMock()) as CharacteristicReadTask

        val bytes = buildTestReading(getFullProtobufModel())
        readTask.onSuccess(bytes)

        assertThat(arbitraryDataImpl.dirty, `is`(false))

        val newKey = "newKey"
        val newValue = "newValue"
        arbitraryDataImpl[newKey] = newValue

        assertThat(arbitraryDataImpl.dirty, `is`(true))
    }

    @Test
    fun testDirtinessWhenEditingEntry() {
        val arbitraryDataImpl = ArbitraryDataProtobufImpl()
        val readTask = arbitraryDataImpl.read(getBluetoothTaskProcessorMock()) as CharacteristicReadTask

        val bytes = buildTestReading(getFullProtobufModel())
        readTask.onSuccess(bytes)

        assertThat(arbitraryDataImpl.dirty, `is`(false))

        arbitraryDataImpl[SAMPLE_KEY_1] = "newValue"

        assertThat(arbitraryDataImpl.dirty, `is`(true))
    }

    @Test
    fun testDirtinessWhenRemovingEntry() {
        val arbitraryDataImpl = ArbitraryDataProtobufImpl()
        val readTask = arbitraryDataImpl.read(getBluetoothTaskProcessorMock()) as CharacteristicReadTask

        val bytes = buildTestReading(getFullProtobufModel())
        readTask.onSuccess(bytes)

        assertThat(arbitraryDataImpl.dirty, `is`(false))

        arbitraryDataImpl.removeEntry(SAMPLE_KEY_1)

        assertThat(arbitraryDataImpl.dirty, `is`(true))
    }


    @Test
    fun testSerializingUnchangedModel() {
        val arbitraryDataImpl = ArbitraryDataProtobufImpl()
        val readTask = arbitraryDataImpl.read(getBluetoothTaskProcessorMock()) as CharacteristicReadTask

        val protobufModel = getFullProtobufModel()
        val bytes = buildTestReading(protobufModel)
        readTask.onSuccess(bytes)

        val serialized = arbitraryDataImpl.serialize()

        assertThat(serialized, `is`(protobufModel.arbitraryData.toByteArray()))
    }

    @Test
    fun testSerializingModelWithAddedEntry() {
        val arbitraryDataImpl = ArbitraryDataProtobufImpl()
        val readTask = arbitraryDataImpl.read(getBluetoothTaskProcessorMock()) as CharacteristicReadTask

        val bytes = buildTestReading(getFullProtobufModel())
        readTask.onSuccess(bytes)

        val newKey = "newKey"
        val newValue = "newValue"
        arbitraryDataImpl[newKey] = newValue

        val serialized = arbitraryDataImpl.serialize()

        val valuesProtobufModel = ArbitraryDataProtobufModel.Values.newBuilder()
            .putValues(SAMPLE_KEY_1, SAMPLE_VALUE_1)
            .putValues(SAMPLE_KEY_2, SAMPLE_VALUE_2)
            .putValues(newKey,newValue)
            .build()

        assertThat(serialized, `is`(valuesProtobufModel.toByteArray()))
    }

    @Test
    fun testSerializingModelWithEditedEntry() {
        val arbitraryDataImpl = ArbitraryDataProtobufImpl()
        val readTask = arbitraryDataImpl.read(getBluetoothTaskProcessorMock()) as CharacteristicReadTask

        val bytes = buildTestReading(getFullProtobufModel())
        readTask.onSuccess(bytes)

        val newValue = "newValue"
        arbitraryDataImpl[SAMPLE_KEY_2] = newValue

        val serialized = arbitraryDataImpl.serialize()

        val valuesProtobufModel = ArbitraryDataProtobufModel.Values.newBuilder()
            .putValues(SAMPLE_KEY_1, SAMPLE_VALUE_1)
            .putValues(SAMPLE_KEY_2, newValue)
            .build()

        assertThat(serialized, `is`(valuesProtobufModel.toByteArray()))
    }

    @Test
    fun testSerializingModelWithRemovedEntry() {
        val arbitraryDataImpl = ArbitraryDataProtobufImpl()
        val readTask = arbitraryDataImpl.read(getBluetoothTaskProcessorMock()) as CharacteristicReadTask

        val bytes = buildTestReading(getFullProtobufModel())
        readTask.onSuccess(bytes)

        arbitraryDataImpl.removeEntry(SAMPLE_KEY_2)

        val serialized = arbitraryDataImpl.serialize()

        val valuesProtobufModel = ArbitraryDataProtobufModel.Values.newBuilder()
            .putValues(SAMPLE_KEY_1, SAMPLE_VALUE_1)
            .build()

        assertThat(serialized, `is`(valuesProtobufModel.toByteArray()))
    }

    @Test
    fun testWritingWithSampleValues() {
        val arbitraryDataImpl = ArbitraryDataProtobufImpl()
        arbitraryDataImpl.capacity = 500

        val taskProcessor = mock(BluetoothTaskProcessor::class.java)
        var writeTask : CharacteristicWriteTask? = null
        `when`(taskProcessor.queueTask(any())).thenAnswer {
            writeTask = it.getArgument<CharacteristicWriteTask>(0)
            true
        }

        arbitraryDataImpl[SAMPLE_KEY_1] = SAMPLE_VALUE_1
        arbitraryDataImpl[SAMPLE_KEY_2] = SAMPLE_VALUE_2
        arbitraryDataImpl.write(taskProcessor,true)

        val serializedProtobufModel = ArbitraryDataProtobufModel.Values.newBuilder()
            .putValues(SAMPLE_KEY_1, SAMPLE_VALUE_1)
            .putValues(SAMPLE_KEY_2, SAMPLE_VALUE_2)
            .build().toByteArray()
        val serializedCrc = UINT32.serialize(CRC32().getValueForUpdate(serializedProtobufModel))

        assertThat("Task processor has not received characteristic write task",
            writeTask, `is`(notNullValue()))
        assertThat(writeTask!!.value, `is`(serializedProtobufModel + serializedCrc))
        assertThat(writeTask!!.name, `is`("Arbitrary Data Write Task"))

    }

    @Test
    fun testWritingEmptyMap() {
        val arbitraryDataImpl = ArbitraryDataProtobufImpl()
        arbitraryDataImpl.capacity = 500

        val taskProcessor = mock(BluetoothTaskProcessor::class.java)
        var writeTask : CharacteristicWriteTask? = null
        `when`(taskProcessor.queueTask(any())).thenAnswer {
            writeTask = it.getArgument<CharacteristicWriteTask>(0)
            true
        }

        arbitraryDataImpl.write(taskProcessor,true)

        verify(taskProcessor,times(1)).queueTask(any())
        assertThat("Task processor has not received characteristic write task",
            writeTask, `is`(notNullValue()))
        assertThat(writeTask!!.value, `is`(byteArrayOf(0,0,0,0))) //4 bytes representing CRC for empty byte array
        assertThat(writeTask!!.name, `is`("Arbitrary Data Write Task"))
    }

    private fun fragmentByteArray(byteArray : ByteArray,fragmentSize : Int) : List<ByteArray> {
        val fragments = mutableListOf<ByteArray>()

        for(i in 0..byteArray.size step fragmentSize) {
            fragments.add(byteArray.sliceArray(IntRange(i,
                (i + fragmentSize - 1).coerceAtMost(byteArray.size - 1)
            )))
        }
        return fragments
    }

    private fun buildTestReading(protobufModel : ArbitraryDataProtobufModel.ArbitraryData) : ByteArray {
        val protobufModelBytes = protobufModel.toByteArray()
        val totalSize = protobufModelBytes.size + 4 //4 bytes is for total size integer
        val byteArray = UINT32.serialize(totalSize.toLong()) + protobufModelBytes
        val crc = CRC32().getValueForUpdate(byteArray)

        return byteArray + UINT32.serialize(crc)
    }

    private fun <T> any(): T = Mockito.any<T>()

    private fun getBluetoothTaskProcessorMock() : BluetoothTaskProcessor {
        val taskProcessor = mock(BluetoothTaskProcessor::class.java)
        `when`(taskProcessor.queueTask(any())).thenReturn(true)
        `when`(taskProcessor.queueTasks(any())).thenReturn(true)
        return taskProcessor
    }


    private fun getSimpleProtobufModel(testCapacity : Int = TEST_CAPACITY) : ArbitraryDataProtobufModel.ArbitraryData {
        return ArbitraryDataProtobufModel.ArbitraryData.newBuilder()
            .setCapacity(testCapacity)
            .build()
    }

    private fun getFullProtobufModel(testCapacity : Int = TEST_CAPACITY) : ArbitraryDataProtobufModel.ArbitraryData {
        return ArbitraryDataProtobufModel.ArbitraryData.newBuilder()
            .setCapacity(testCapacity)
            .setArbitraryData(
                ArbitraryDataProtobufModel.Values.newBuilder()
                    .putValues(SAMPLE_KEY_1, SAMPLE_VALUE_1)
                    .putValues(SAMPLE_KEY_2, SAMPLE_VALUE_2)
            )
            .build()
    }

    private fun verifyFullProtobufModel(arbitraryDataImpl : ArbitraryDataProtobufImpl) {
        assertThat(arbitraryDataImpl.capacity, `is`(TEST_CAPACITY))
        assertThat(arbitraryDataImpl[SAMPLE_KEY_1], `is`(SAMPLE_VALUE_1))
        assertThat(arbitraryDataImpl[SAMPLE_KEY_2], `is`(SAMPLE_VALUE_2))
        assertThat(arbitraryDataImpl.entries.size, `is`(2))
    }



    companion object {
        const val SAMPLE_KEY_1 = "key1"
        const val SAMPLE_KEY_2 = "key2"
        const val SAMPLE_VALUE_1 = "value1"
        const val SAMPLE_VALUE_2 = "value2"
        const val TEST_CAPACITY = 155
    }

}