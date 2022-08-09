package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.PublishEntity
import com.aconno.sensorics.domain.ifttt.BasePublish

interface PublishEntityMapper<P, E> : EntityMapper<P, E> where P : BasePublish, E : PublishEntity