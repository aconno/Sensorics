package com.aconno.sensorics.data.repository.googlepublish

import com.aconno.sensorics.data.mapper.GooglePublishDataMapper
import com.aconno.sensorics.data.repository.PublishRepositoryImpl
import com.aconno.sensorics.domain.ifttt.GooglePublish
import com.aconno.sensorics.domain.ifttt.publish.GooglePublishRepository

class GooglePublishRepositoryImpl(
    googlePublishDao: GooglePublishDao,
    googlePublishDataMapper: GooglePublishDataMapper
) : PublishRepositoryImpl<GooglePublish, GooglePublishEntity>(
    googlePublishDao,
    googlePublishDataMapper
), GooglePublishRepository