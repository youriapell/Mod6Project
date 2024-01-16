package org.tensorflow.lite.examples.poseestimation.ml

import android.graphics.PointF
import org.tensorflow.lite.examples.poseestimation.data.Person
import org.tensorflow.lite.examples.poseestimation.data.BodyPart
import kotlin.math.atan2

class PushUpDetector {
    fun isCorrectPushUp(person: Person): Boolean {
        val keypoints = person.keyPoints
        val leftShoulder = keypoints.firstOrNull { it.bodyPart == BodyPart.LEFT_SHOULDER }
        val rightShoulder = keypoints.firstOrNull { it.bodyPart == BodyPart.RIGHT_SHOULDER }
        val leftElbow = keypoints.firstOrNull { it.bodyPart == BodyPart.LEFT_ELBOW }
        val rightElbow = keypoints.firstOrNull { it.bodyPart == BodyPart.RIGHT_ELBOW }
        val leftWrist = keypoints.firstOrNull { it.bodyPart == BodyPart.LEFT_WRIST }
        val rightWrist = keypoints.firstOrNull { it.bodyPart == BodyPart.RIGHT_WRIST }
        val rightHip = keypoints.firstOrNull { it.bodyPart == BodyPart.RIGHT_HIP }
        val leftHip = keypoints.firstOrNull { it.bodyPart == BodyPart.LEFT_HIP }
        val rightKnee = keypoints.firstOrNull { it.bodyPart == BodyPart.RIGHT_KNEE }
        val leftKnee = keypoints.firstOrNull { it.bodyPart == BodyPart.LEFT_KNEE }


        if (leftShoulder == null || rightShoulder == null ||
            leftElbow == null || rightElbow == null ||
            leftWrist == null || rightWrist == null ||
            leftHip == null || rightHip == null ||
            leftKnee == null || rightKnee == null
        ) {
            return false
        }

        val rightElbowAngle = elbowAngle(rightShoulder.coordinate, rightElbow.coordinate, rightWrist.coordinate)
        val leftElbowAngle = elbowAngle(leftShoulder.coordinate, leftElbow.coordinate, leftWrist.coordinate)

        val rightSideBodyAngle = straightBack(rightShoulder.coordinate, rightElbow.coordinate, rightWrist.coordinate)
        val leftSideBodyAngle = straightBack(leftShoulder.coordinate, leftElbow.coordinate, leftWrist.coordinate)

        return rightElbowAngle && leftElbowAngle && rightSideBodyAngle && rightSideBodyAngle
    }

    private fun elbowAngle(shoulder: PointF, elbow: PointF, wrist: PointF) : Boolean {
        val elbowAngle = calculateAngle(shoulder, elbow, wrist)

        return elbowAngle in PUSH_UP_ELBOW_ANGLE_RANGE
    }

    private fun straightBack(shoulder: PointF, hip: PointF, knee: PointF) : Boolean {
        val bodyAngle = calculateAngle(shoulder, hip, knee)

        return bodyAngle in (0.0..10.0) || bodyAngle in (170.0..180.0)
    }

    private fun calculateAngle(firstPoint: PointF, midPoint: PointF, lastPoint: PointF): Double {
        val radians = atan2(lastPoint.y - midPoint.y, lastPoint.x - midPoint.x) -
                atan2(firstPoint.y - midPoint.y, firstPoint.x - midPoint.x)
        return Math.toDegrees(radians.toDouble()).mod(180.0)
    }


    companion object {
        private val PUSH_UP_ELBOW_ANGLE_RANGE = 70.0..110.0
    }



}