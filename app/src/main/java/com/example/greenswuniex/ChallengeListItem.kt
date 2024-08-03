package com.example.greenswuniex

data class ChallengeListItem(
    var challenge_onCheck: Boolean = false,
    var challenge_category: String = "기본 카테고리",
    var challenge_name: String = "기본 챌린지 이름"
)