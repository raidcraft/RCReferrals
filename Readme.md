# RCReferrals

[![Build Status](https://github.com/raidcraft/rcreferrals/workflows/Build/badge.svg)](../../actions?query=workflow%3ABuild)
[![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/raidcraft/rcreferrals?include_prereleases&label=release)](../../releases)
[![codecov](https://codecov.io/gh/raidcraft/rcreferrals/branch/master/graph/badge.svg)](https://codecov.io/gh/raidcraft/rcreferrals)
[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)
[![semantic-release](https://img.shields.io/badge/%20%20%F0%9F%93%A6%F0%9F%9A%80-semantic--release-e10079.svg)](https://github.com/semantic-release/semantic-release)

A simple plugin that allows players to tell who referred them and both get rewards.

## Configuration

```yaml
# The timeout after which players cannot say why they came to the server.
# 1h, 1d, etc.
referral_timeout: "0s"
# The time in ticks to wait until posting the referral message to the player.
login_message_delay: 1200
types:
  # define various referral types here
  # the player gets listed all active types and can choose
  vote:
    name: Voting Top Liste
    description: Du bist über eine der Server Listen auf uns gestoßen.
    text: "eine "
    active: true
# An ART list of rewards new players get when they answer how they found the server.
new_player_rewards:
  - '!item diamond 20'
  - '!money 1000'
  - '!rcskills:exp 1000'
# An ART list of rewards for players that referred other players
player_rewards:
  - '!loot.create common'
  - '?referrals >5'
  - '!item diamond 20'
database:
  username: sa
  password: sa
  driver: h2
  url: "jdbc:h2:~/referrals.db"
```