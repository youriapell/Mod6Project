package org.tensorflow.lite.examples.poseestimation.ml

import android.graphics.PointF
import android.media.MediaPlayer
import android.view.SurfaceView
import org.tensorflow.lite.examples.poseestimation.R
import org.tensorflow.lite.examples.poseestimation.data.Person
import org.tensorflow.lite.examples.poseestimation.data.BodyPart
import kotlin.math.atan2

class PushUpDetector(
    private val surfaceView: SurfaceView) {
    private var rightElbowCounter: Int = 0
    private var rightSideBodyCounter: Int = 0
    private val ERROR_MARGIN: Int = 20
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

        val rightElbowAngle = rightElbowAngle(rightShoulder.coordinate, rightElbow.coordinate, rightWrist.coordinate)
       // val leftElbowAngle = elbowAngle(leftShoulder.coordinate, leftElbow.coordinate, leftWrist.coordinate)

        val rightSideBodyAngle = straightBackRight(rightShoulder.coordinate, rightHip.coordinate, rightKnee.coordinate)
        //val leftSideBodyAngle = straightBack(leftShoulder.coordinate, leftElbow.coordinate, leftWrist.coordinate)

        return  rightElbowAngle && rightSideBodyAngle
    }

    private fun rightElbowAngle(shoulder: PointF, elbow: PointF, wrist: PointF) : Boolean {
        val elbowAngle = calculateAngle(shoulder, elbow, wrist)

        //println("Elbow angle: $elbowAngle")
        println("Right elbow counter: $rightElbowCounter")

        var angleInRange = elbowAngle in PUSH_UP_ELBOW_ANGLE_RANGE
        if(angleInRange){
            rightElbowCounter = 0
        }else {
            rightElbowCounter++
            if (rightElbowCounter > ERROR_MARGIN){
                rightElbowCounter = 0
                println("Your elbows are wrong!!!")
                try {
                    var mp = MediaPlayer.create(surfaceView.context, R.raw.elbows)
                    mp.start()

                    mp.setOnCompletionListener { player ->
                        player.release()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return false
            }
        }

        return true
    }

    private fun straightBackRight(shoulder: PointF, hip: PointF, knee: PointF) : Boolean {
        val bodyAngle = calculateAngle(shoulder, hip, knee)

      // println("Body angle: $bodyAngle")
      println("Right body counter: $rightSideBodyCounter")

        var angleInRange = bodyAngle in (0.0..20.0) || bodyAngle in (160.0..180.0)
        if(angleInRange){
            rightSideBodyCounter = 0
        }else {
            rightSideBodyCounter++
            if (rightSideBodyCounter > ERROR_MARGIN){
                rightSideBodyCounter = 0
                println("Straighten your back!!!")
                try {
                    var mp = MediaPlayer.create(surfaceView.context, R.raw.back)
                    mp.start()

                    mp.setOnCompletionListener { player ->
                        player.release()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return false
            }
        }
        return true
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